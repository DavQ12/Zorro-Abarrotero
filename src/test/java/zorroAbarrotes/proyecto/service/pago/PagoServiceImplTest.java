package zorroAbarrotes.proyecto.service.pago;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zorroAbarrotes.proyecto.model.entity.PagoEntity;

@SpringBootTest
class PagoServiceImplTest {

    @Autowired
    PagoService pagoService;

    @Test
    void salvar(){
        PagoEntity pago = PagoEntity.builder()
                .descripcion("Mercado Pago")
                .build();

        pagoService.save(pago);
    }

}