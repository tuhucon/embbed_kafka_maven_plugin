package sample.plugin;


import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 */
@Mojo( name = "start", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST )
public class StartMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    private ClassLoader getClassLoader() throws MojoExecutionException
    {
        try
        {
            List<String> classpathElements = project.getTestClasspathElements();
            getLog().error("TUHUCON: " + classpathElements);
            classpathElements.add(project.getBuild().getOutputDirectory() );
            classpathElements.add(project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];

            for ( int i = 0; i < classpathElements.size(); ++i )
            {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader() );
        }
        catch (Exception e)//gotta catch em all
        {
            getLog().error(e);
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException {
//        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(getClassLoader());

        try {
            ClassLoader x = EmbbededServices.class.getClassLoader();

            getLog().info("TUHUCON: START");
            getLog().info("TUHUCON: System " + System.identityHashCode(x));
            if (EmbbededServices.embeddedKafkaCluster == null) {
                getLog().info("TUHUCON: " + ProcessHandle.current().pid() + " " + Thread.currentThread().getId());
                getLog().info("TUHUCON: " + System.identityHashCode(EmbbededServices.class));
                while (x != null) {
                    getLog().info("TUHUCON: " + x.toString());
                    x = x.getParent();
                }
                EmbbededServices.embeddedKafkaCluster = EmbeddedKafkaCluster.provisionWith(EmbeddedKafkaClusterConfig.useDefaults());
                EmbbededServices.embeddedKafkaCluster.start();
            }
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage());
        }
    }
}
