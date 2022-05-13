package dds.monedero.model;

import dds.monedero.exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    this.chequearMontoInicial(montoInicial);
    this.saldo = montoInicial;
  }

  private void chequearMontoInicial(double montoInicial) {

    if (montoInicial < 0) {
      throw new InicializacionSaldoIncorrectoException("El monto inicial deber ser mayor o igual a 0");
    }
  }

  public void setMovimientos(List<Movimiento> movimientos) {

    this.chequearMovimientos(movimientos);
    this.movimientos = movimientos;
  }

  private void chequearMovimientos(List<Movimiento> movimientos) {

    if (movimientos == null) {

      throw new MovimientosInvalidosException("Los movimientos cargados deben ser una lista de movimientos");
    }
  }

  public void depositar(double monto) {
    
    this.chequearMontoDespositado(monto);
    
    new Movimiento(LocalDate.now(), monto, true).agregateA(this);
  }
  
  private void chequearMontoDespositado(double monto) {

    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (this.superaCantidadDepositosDiarios()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private boolean superaCantidadDepositosDiarios() {
    
    return this.movimientos.stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3;
  }

  public void sacar(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return this.movimientos.stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
