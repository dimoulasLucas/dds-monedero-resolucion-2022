package dds.monedero.model;

import dds.monedero.exceptions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private final double limite = 1000;

  public Cuenta(double montoInicial) {
    this.chequearMontoInicial(montoInicial);
    this.saldo = montoInicial;
  }

  public double getSaldo() {
    return saldo;
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

    Movimiento movimiento = new Movimiento(LocalDate.now(), monto, true);
    this.agregarMovimiento(movimiento);
    this.saldo += monto;
  }
  
  private void chequearMontoDespositado(double monto) {

    this.chequearMontoNoNegativo(monto);

    if (this.superaCantidadDepositosDiarios()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private boolean superaCantidadDepositosDiarios() {
    
    return this.movimientos.stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3;
  }

  public void extraer(double monto) {

    this.chequearMontoExtraido(monto);
    this.agregarMovimiento(new Movimiento(LocalDate.now(), monto, true));
  }

  private void chequearMontoExtraido(double monto) {

    this.chequearMontoNoNegativo(monto);
    if ((this.saldo - monto) < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + this.saldo + " $");
    }

    if (this.intentaExtraerMontoMayorAlDiario(monto)) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + limite
          + " diarios, límiteExtraccionRestante: " + this.getMontoExtraidoA(LocalDate.now()));
    }
  }

  private void chequearMontoNoNegativo(double monto) {

    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private boolean intentaExtraerMontoMayorAlDiario(double monto) {

    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limiteExtraccionRestante = this.limite - montoExtraidoHoy;

    return monto > limiteExtraccionRestante;
  }

  private void agregarMovimiento(Movimiento movimiento) {
    this.movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return this.movimientos.stream()
        .filter(movimiento -> movimiento.esDepositoEnFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }
}
