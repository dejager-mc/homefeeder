package nl.dejagermc.homefeeder.input.openhab;

import lombok.extern.slf4j.Slf4j;
import nl.dejagermc.homefeeder.input.openhab.model.OpenhabItem;
import nl.dejagermc.homefeeder.input.openhab.repository.OpenhabItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Set;

import static nl.dejagermc.homefeeder.input.openhab.builders.OpenhabItemBuilders.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenhabInputService.class)
@EnableConfigurationProperties
@Slf4j
public class OpenhabInputServiceTest {

    @Autowired
    private OpenhabInputService openhabInputService;

    @MockBean
    private OpenhabItemRepository openhabItemRepository;

    private OpenhabItem tvItem = tvItem("1");
    private OpenhabItem tvStreamItem = tvStreamItem("1");
    private OpenhabItem tvItem2 = tvItem("2");
    private OpenhabItem tvStreamItem2 = tvStreamItem("2");
    private OpenhabItem switchItem = switchItem();
    private OpenhabItem stringItem = stringItem();
    private Set<OpenhabItem> openhabItems;

    @Before
    public void setup() {
        log.info("Loading specific getAllDeliveries setup for {}...", this.getClass().getSimpleName());

        openhabItems = Set.of(
                tvItem,
                tvItem2,
                tvStreamItem,
                tvStreamItem2,
                switchItem,
                stringItem
        );
    }

    @Test
    public void testGetAllItems() {
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        Set<OpenhabItem> results = openhabInputService.getAllOpenhabItems();
        validateMockitoUsage();

        assertEquals(6, results.size());
        assertThat(results, containsInAnyOrder(tvItem, tvItem2, tvStreamItem, tvStreamItem2, switchItem, stringItem));
    }

    @Test
    public void testGetItemByName() {
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        Optional<OpenhabItem> result = openhabInputService.findOpenhabItemWithName("string_name");
        assertTrue(result.isPresent());
        assertEquals(stringItem, result.get());
    }

    @Test
    public void testGetItemByNameNothingFound() {
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        Optional<OpenhabItem> result = openhabInputService.findOpenhabItemWithName("Non existing name");
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetItemByLabel() {
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        Optional<OpenhabItem> result = openhabInputService.findOpenhabItemWithLabel("string item");
        assertTrue(result.isPresent());
        assertEquals(stringItem, result.get());
    }

    @Test
    public void testGetItemByLabelNothingFound() {
        when(openhabItemRepository.getAllOpenhabItems()).thenReturn(openhabItems);
        Optional<OpenhabItem> result = openhabInputService.findOpenhabItemWithLabel("Non existing label");
        assertFalse(result.isPresent());
    }
}
