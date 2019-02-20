package nl.dejagermc.homefeeder.schedulers;

import nl.dejagermc.homefeeder.schudulers.DotaScheduler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DotaScheduler.class)
@EnableConfigurationProperties
public class DotaSchedulerTest {

    @Autowired
    private DotaScheduler dotaScheduler;
}
