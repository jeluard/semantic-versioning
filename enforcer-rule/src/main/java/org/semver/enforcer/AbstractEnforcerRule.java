/**
 * Copyright 2012-2014 Julien Eluard and contributors
 * This project includes software developed by Julien Eluard: https://github.com/jeluard/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semver.enforcer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.semver.Comparer;
import org.semver.Delta;
import org.semver.Dumper;
import org.semver.Version;

/**
 * Abstract {@link EnforcerRule} implementation providing facilities for compatibility checking.
 */
public abstract class AbstractEnforcerRule implements EnforcerRule {

    private static final String JAR_ARTIFACT_TYPE = "jar";
    private static final String BUNDLE_ARTIFACT_TYPE = "bundle";
    
    /**
     * Version number of artifact to be checked.
     *
     * @parameter
     */
    private String previousVersion;        
    
    /**
     * Class names to be included.
     *
     * @parameter
     */
    private String[] includes;    
    
    /**
     * Class names to be excluded.
     *
     * @parameter
     */
    private String[] excludes;
    
    /**
     * Dump change details.
     *
     * @parameter
     */ 
    private boolean dumpDetails = false;
    
    private Set<String> extractFilters(final String[] filtersAsStringArray) {
        if (filtersAsStringArray == null) {
            return Collections.emptySet();
        }
        return new HashSet<String>(Arrays.asList(filtersAsStringArray));
    }
    
    @Override
    public void execute(final EnforcerRuleHelper helper) throws EnforcerRuleException {
        final MavenProject project;
        try {
            project = (MavenProject) helper.evaluate("${project}");
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Failed to access ${project} variable", e);
        }
        final String type = project.getArtifact().getType();
        if (!AbstractEnforcerRule.JAR_ARTIFACT_TYPE.equals(type) &&
            !AbstractEnforcerRule.BUNDLE_ARTIFACT_TYPE.equals(type)) {
            helper.getLog().debug("Skipping non "+AbstractEnforcerRule.JAR_ARTIFACT_TYPE+
              " or " + BUNDLE_ARTIFACT_TYPE + " artifact.");
            return;
        }

        final Artifact previousArtifact;
        final Artifact currentArtifact = project.getArtifact();
        validateArtifact(currentArtifact);
        final Version current = Version.parse(currentArtifact.getVersion());
        final File currentJar = currentArtifact.getFile();
        try {
            final ArtifactRepository localRepository = (ArtifactRepository) helper.evaluate("${localRepository}");
            final String version;
            if (this.previousVersion != null) {
                version = this.previousVersion;

                helper.getLog().info("Version specified as <"+version+">");
            } else {                
                final ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) helper.getComponent(ArtifactMetadataSource.class);
                final List<ArtifactVersion> availableVersions = getAvailableReleasedVersions(artifactMetadataSource, project, localRepository);
                final List<ArtifactVersion> availablePreviousVersions = filterNonPreviousVersions(availableVersions, current);
                
                if (availablePreviousVersions.isEmpty()) {
                    helper.getLog().warn("No previously released version. Backward compatibility check not performed.");
                    
                    return;
                }

                version = availablePreviousVersions.iterator().next().toString();
                
                helper.getLog().info("Version deduced as <"+version+"> (among all availables: "+availablePreviousVersions+")");
            }
            
            final ArtifactFactory artifactFactory = (ArtifactFactory) helper.getComponent(ArtifactFactory.class);
            previousArtifact = artifactFactory.createArtifact(project.getGroupId(), project.getArtifactId(), version, null, type);
            final ArtifactResolver resolver = (ArtifactResolver) helper.getComponent(ArtifactResolver.class );
            resolver.resolve(previousArtifact, project.getRemoteArtifactRepositories(), localRepository);

            validateArtifact(previousArtifact);
        } catch (Exception e) {
            helper.getLog().warn("Exception while accessing artifacts; skipping check.", e);
            return;
        }     

        final Version previous = Version.parse(previousArtifact.getVersion());
        final File previousJar = previousArtifact.getFile();

        helper.getLog().info("Using <"+previousJar+"> as previous JAR");
        helper.getLog().info("Using <"+currentJar+"> as current JAR");
        
        try {
            final Comparer comparer = new Comparer(previousJar, currentJar, extractFilters(this.includes), extractFilters(this.excludes));
            final Delta delta = comparer.diff();

            enforce(helper, delta, previous, current);
        } catch (IOException e) {
            throw new EnforcerRuleException("Exception while checking compatibility: "+e.toString(), e);
        }
    }

    protected abstract void enforce(final EnforcerRuleHelper helper, final Delta delta, final Version previous, final Version current) throws EnforcerRuleException;
    
    protected final void fail(final Delta delta, final String message) throws EnforcerRuleException {
        if (this.dumpDetails) {
            Dumper.dump(delta);
        }
        throw new EnforcerRuleException(message);
    }
    
    /**
     * @param artifactMetadataSource
     * @param project
     * @param localRepository
     * @return all available versions from most recent to oldest
     * @throws ArtifactMetadataRetrievalException 
     */
    protected final List<ArtifactVersion> getAvailableReleasedVersions(final ArtifactMetadataSource artifactMetadataSource, final MavenProject project, final ArtifactRepository localRepository) throws ArtifactMetadataRetrievalException {
        final List<ArtifactVersion> availableVersions = artifactMetadataSource.retrieveAvailableVersions(project.getArtifact(), localRepository, project.getRemoteArtifactRepositories());
        availableVersions.remove(new DefaultArtifactVersion(project.getArtifact().getVersion()));
        for (final Iterator<ArtifactVersion> iterator = availableVersions.iterator(); iterator.hasNext();) {
            final ArtifactVersion artifactVersion = iterator.next();
            if (Version.parse(artifactVersion.toString()).isSnapshot()) {
                iterator.remove();
            }
        }
        //TODO proper sorting based on Version
        Collections.sort(availableVersions);
        Collections.reverse(availableVersions);
        return availableVersions;
    }

    protected final List<ArtifactVersion> filterNonPreviousVersions(final List<ArtifactVersion> availableVersions, final Version version) {
        final List<ArtifactVersion> versions = new ArrayList<ArtifactVersion>();
        for (final ArtifactVersion artifactVersion : availableVersions) {
            if (version.compareTo(Version.parse(artifactVersion.toString())) > 0) {
                versions.add(artifactVersion);
            }
        }
        return versions;
    }

    /**
     * Validates that specified {@link Artifact} is a file.
     * @param artifact
     */
    private void validateArtifact(final Artifact artifact) {
        if (!artifact.getFile().isFile()) {
            throw new IllegalArgumentException("<"+artifact.getFile()+"> is not a file");
        }
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(final EnforcerRule cachedRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return "0";
    }

}
