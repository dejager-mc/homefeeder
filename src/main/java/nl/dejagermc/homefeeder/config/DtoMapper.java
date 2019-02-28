package nl.dejagermc.homefeeder.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
