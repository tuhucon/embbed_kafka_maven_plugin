package sample.plugin;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo( name = "stop", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST )
public class StopMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private ClassLoader getClassLoader() throws MojoExecutionException
    {
        try
        {
            List<String> classpathElements = project.getCompileClasspathElements();
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
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }
    @Override
    public void execute() throws MojoExecutionException {
//        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(getClassLoader());
        getLog().info("TUHUCON: STOP");
        if (EmbbededServices.embeddedKafkaCluster != null) {
            getLog().info("TUHUCON: " + ProcessHandle.current().pid() + " " + Thread.currentThread().getId());
            getLog().info("TUHUCON: " + EmbbededServices.class.getClassLoader().toString());
            EmbbededServices.embeddedKafkaCluster.stop();
        }
    }
}
