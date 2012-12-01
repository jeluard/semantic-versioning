/**
 * Copyright 2012 Julien Eluard
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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.semver.Delta;
import org.semver.Version;

/**
 * 
 * Checks {@link Version} for current {@link Artifact} compared to a previous {@link Artifact}.
 * <br />
 * Fails if {@link Version} semantic is not respected.
 * 
 */
public final class RequireSemanticVersioningConformance extends AbstractEnforcerRule {

    @Override
    protected void enforce(final EnforcerRuleHelper helper, final Delta delta, final Version previous, final Version current) throws EnforcerRuleException {
        final boolean compatible = delta.validate(previous, current);
        if (!compatible) {
            fail(delta, "Current codebase is incompatible with version <"+previous+">. Version should be at least <"+delta.infer(previous)+">.");
        }
    }

}
