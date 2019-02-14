package nl.dejagermc.homefeeder;

import nl.dejagermc.homefeeder.config.TestHomeFeederConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestHomeFeederConfig.class)
public class MainTest {
}
