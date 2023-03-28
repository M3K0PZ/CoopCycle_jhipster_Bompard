package noah.bompard.coopcycle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasketMapperTest {

    private BasketMapper basketMapper;

    @BeforeEach
    public void setUp() {
        basketMapper = new BasketMapperImpl();
    }
}
