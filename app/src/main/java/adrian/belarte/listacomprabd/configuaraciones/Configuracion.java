package adrian.belarte.listacomprabd.configuaraciones;

import java.text.NumberFormat;

public class Configuracion {
    public static final String BD_NAME = "tienda.bd";
    public static final int BD_VERSION = 1;
    public final NumberFormat NF = NumberFormat.getNumberInstance();
    public static final NumberFormat NFM = NumberFormat.getCurrencyInstance();
}
