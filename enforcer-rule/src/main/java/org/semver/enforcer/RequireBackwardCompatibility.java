/**
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2010 Julien Eluard
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     [http://www.apache.org/licenses/LICENSE-2.0]
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.semver.enforcer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.semver.Delta;
import org.semver.Version;

/**
 * 
 * Checks {@link Version} for current {@link Artifact} compared to a previous {@link Artifact}.
 * <br />
 * Fails if current version is not backward compatible with previous one. {@link Delta.CompatibilityType} level can be specified.
 * 
 */
public final class RequireBackwardCompatibility extends AbstractEnforcerRule {
    
    /**
     * Compatibility type expected. Must be one of {@link Delta.CompatibilityType} enum values.
     * 
     * @parameter
     */
    private String compatibilityType;

    @Override
    protected void enforce(final Delta delta, final Version previous, final Version current) throws EnforcerRuleException {
        if (this.compatibilityType == null) {
            throw new IllegalArgumentException("A value for compatibilityType attribute must be provided.");
        }
        
        final Delta.CompatibilityType expectedCompatibilityType;
        try {
            expectedCompatibilityType = Delta.CompatibilityType.valueOf(this.compatibilityType);
        } catch (IllegalStateException e) {
            throw new EnforcerRuleException("Compatibility type value must be one of "+Delta.CompatibilityType.values());
        }

        final Delta.CompatibilityType detectedCompatibilityType = delta.computeCompatibilityType();
        if (detectedCompatibilityType != expectedCompatibilityType) {
            fail(delta, "Current codebase is not backward compatible ("+this.compatibilityType+") with version <"+previous+">. Compatibility type has been detected as <"+detectedCompatibilityType+">");
        }
    }

}
