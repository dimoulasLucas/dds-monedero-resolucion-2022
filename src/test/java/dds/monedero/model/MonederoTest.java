package dds.monedero.model;

import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta(0);
  }

  @Test
  void ponerUnSaldoPositivoValidoNoFalla() {
    assertDoesNotThrow(() -> cuenta.depositar(1500));
  }

  @Test
  void ponerUnSaldoNegativoInvalidoFalla() {
    assertThrows(MontoNegativoException.class, () -> cuenta.depositar(-1500));
  }

  @Test
  void saldoFinalEsIgualALaSumaDeSusDepositos() {
    cuenta.depositar(1500);
    cuenta.depositar(456);
    cuenta.depositar(1900);

    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  void extraerMasQueElSaldoDisponibleFalla() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.depositar(90);
          cuenta.extraer(1001);
    });
  }

  @Test
  public void extraerMasDe1000PesosDiariosFalla() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.depositar(5000);
      cuenta.extraer(1001);
    });
  }

  @Test
  public void extraerMontoNegativoDelSaldoFalla() {
    assertThrows(MontoNegativoException.class, () -> {
      cuenta.depositar(1000);
      cuenta.extraer(-500);
    });
  }

  @Test
  public void extraerMontoIgualA0Falla() {
    assertThrows(MontoNegativoException.class, () -> {
      cuenta.depositar(1000);
      cuenta.extraer(0);
    });
  }

}