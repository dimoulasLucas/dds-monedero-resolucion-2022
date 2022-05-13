package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
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
    cuenta = new Cuenta();
  }

  @Test
  void ponerUnSaldoPositivoValidoNoFalla() {
    assertDoesNotThrow(() -> cuenta.poner(1500));
  }

  @Test
  void ponerUnSaldoNegativoInvalidoFalla() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void saldoFinalEsIgualALaSumaDeSusDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);

    assertEquals(3856, cuenta.getSaldo());
  }

  @Test
  void extraerMasQueElSaldoDisponibleFalla() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.poner(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void extraerMasDe1000PesosDiariosFalla() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void extraerMontoNegativoDelSaldoFalla() {
    assertThrows(MontoNegativoException.class, () -> {
      cuenta.poner(1000);
      cuenta.sacar(-500);
    });
  }

  @Test
  public void extraerMontoIgualA0Falla() {
    assertThrows(MontoNegativoException.class, () -> {
      cuenta.poner(1000);
      cuenta.sacar(0);
    });
  }

}