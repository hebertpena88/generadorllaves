/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CXI;

import CryptoServerAPI.CryptoServerException;
import CryptoServerCXI.CryptoServerCXI;
import CryptoServerCXI.CryptoServerCXI.KeyAttributes;
import Log.Log;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author dotNet
 */
public class CXI {
    private CryptoServerCXI cxi;
    private Boolean Estatus;
    private KeyAttributes[] lista;
    public String Conectar(String usuario,String password,String ip)
    {
        try {
             String mensaje = Validar(usuario, password, ip);
            if(!mensaje.equals("") )
                return mensaje;

            mensaje =Inicializar(ip);
            if(!mensaje.equals("") )
                return mensaje;
            cxi.setTimeout(20000);
            cxi.logonPassword(usuario, password);
             
            Estatus=true;
            return "";
            
            
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error en la autenticacion, verifique que el usuario y la contraseña son correctas";
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error en la autenticacion, verifique que el usuario y la contraseña son correctas";
        }
    }

    private String Inicializar(String ip)
    {
        try {
            cxi = new CryptoServerCXI(ip, 3000);
            getCxi().setTimeout(60000);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        } catch (NumberFormatException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        }
    }

    private String Validar(String usuario,String password,String ip)
    {
        if(usuario == null || password == null || ip == null)
            return "Parámetros incompletos";
        else if(usuario.equals("") || password.equals("") || ip.equals(""))
            return "Parámetros incompletos";
        else
            return "";
    }

    public String GenerarLlave(String nombre,String grupo)
    {
        String mensaje="";
        if(Estatus == true)
        {
            try
            {
              CryptoServerCXI.KeyAttributes attr = new CryptoServerCXI.KeyAttributes();

              attr = new CryptoServerCXI.KeyAttributes();
              attr.setAlgo(CryptoServerCXI.KEY_ALGO_RSA);
              attr.setSize(1024);
              attr.setName(nombre);
              attr.setLabel(nombre);
              if(!grupo.equals(""))
                  attr.setGroup(grupo);
              attr.setExport(CryptoServerCXI.KEY_EXPORT_ALLOW);
              attr.setExponent(BigInteger.valueOf(65537L));
              attr.setGenerationDate(new Date());
              attr.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-21 11:55:00"));

              CryptoServerCXI.Key rsaKey = cxi.generateKey(CryptoServerCXI.FLAG_OVERWRITE, attr);
            } catch (ParseException ex) {
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                mensaje="Ocurrio un error al tratar de generar la llave";
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CryptoServerException ex) {
                mensaje="Ocurrio un error al tratar de generar la llave";
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            }
            return mensaje;
        }
        else
        return "El dispositivo no esta CONECTADO, por favor, ingrese sus credenciales";
    }

     public byte[] ObtenerFirma(byte[] data,String nombreLlave,String grupo) throws IOException, NumberFormatException, CryptoServerException {
        byte[] sign=null;
        Log log = new Log();
        try {
            log.Write("Buscando la llave: " + nombreLlave);
            KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals(""))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            // hash data
            MessageDigest md = MessageDigest.getInstance("SHA-1", "SUN");
            md.update(data, 0, data.length);
            byte[] hash = md.digest();
            // RSA sign hash
            int mech = CryptoServerCXI.MECH_HASH_ALGO_SHA1 |CryptoServerCXI.MECH_PAD_PKCS1 ;
            sign = cxi.sign( rsaKey, mech, hash);
            log.Write("Se firmó correctamente la petición");

        } catch (Exception ex) {
            log.Write("Ocurrió un error al firmar la petición: " + ex.getMessage());
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
            return sign;
    }

     public String ObtenerModulus(String nombreLlave,String grupo)
    {     
        try {
            KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals(""))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            KeyAttributes attr2 =cxi.getKeyAttributes(rsaKey, true);
             StringBuilder sb = new StringBuilder();
             byte[] byteArray=attr2.getModulus();

         String hex = DatatypeConverter.printHexBinary(byteArray);

            
            return hex;
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
     }
    /**
     * @return the cxi
     */
    public CryptoServerCXI getCxi() {
        return cxi;
    }

    /**
     * @return the Estatus
     */
    public Boolean getEstatus() {
        return Estatus;
    }

    /**
     * @return the lista
     */
    public KeyAttributes[] getLista() {
        try {
            return cxi.listKeys();
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
