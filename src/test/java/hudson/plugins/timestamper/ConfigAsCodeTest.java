package hudson.plugins.timestamper;

import hudson.ExtensionList;
import io.jenkins.plugins.casc.ConfigurationContext;
import io.jenkins.plugins.casc.Configurator;
import io.jenkins.plugins.casc.ConfiguratorRegistry;
import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import io.jenkins.plugins.casc.model.CNode;
import io.jenkins.plugins.casc.model.Mapping;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigAsCodeTest {

    @Rule public JenkinsConfiguredWithCodeRule r = new JenkinsConfiguredWithCodeRule();

    @Test
    @ConfiguredWithCode("configuration-as-code.yml")
    public void should_support_configuration_as_code() {
        TimestamperConfig timestamperConfig = TimestamperConfig.get();
        assertTrue(timestamperConfig.isAllPipelines());
        assertEquals(timestamperConfig.getElapsedTimeFormat(), "'<b>'HH:mm:ss'</b> '");
        assertEquals(timestamperConfig.getSystemTimeFormat(), "'<b>'mm:ss'</b> '");
    }

    @Test
    @ConfiguredWithCode("configuration-as-code.yml")
    @SuppressWarnings("unchecked")
    public void export_configuration() throws Exception {
        final TimestamperConfig metricsDescriptors = ExtensionList.lookupSingleton(TimestamperConfig.class);

        ConfiguratorRegistry registry = ConfiguratorRegistry.get();
        ConfigurationContext context = new ConfigurationContext(registry);
        final Configurator c = context.lookupOrFail(TimestamperConfig.class);
        final CNode node = c.describe(metricsDescriptors, context);
        assertNotNull(node);
        final Mapping accessKey = node.asMapping();

        assertTrue(Boolean.parseBoolean(accessKey.getScalarValue("allPipelines")));
        assertEquals(accessKey.getScalarValue("elapsedTimeFormat"), "'<b>'HH:mm:ss'</b> '");
        assertEquals(accessKey.getScalarValue("systemTimeFormat"), "'<b>'mm:ss'</b> '");
    }
}