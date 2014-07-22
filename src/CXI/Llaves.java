/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package CXI;

import CryptoServerAPI.CryptoServerException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */



public class Llaves   {
    
  private byte[] Modulus;
  private String Nombre;
  private String Grupo;
  private String Label;
  private CryptoServerCXI.CryptoServerCXI.Key llave;
  
 
  public Llaves(CryptoServerCXI.CryptoServerCXI.Key key){
      try {
          Modulus =     key.getAttributes().getModulus();
          Nombre=       key.getAttributes().getName();
          Grupo =       key.getAttributes().getGroup();
          llave = key;
          Label = key.getAttributes().getLabel();
      } catch (CryptoServerException ex) {
          Logger.getLogger(Llaves.class.getName()).log(Level.SEVERE, null, ex);
      }
     

  }
  
  public Llaves( 
                CryptoServerCXI.CryptoServerCXI.Key key,
                CryptoServerCXI.CryptoServerCXI cxi){
      try {
          CryptoServerCXI.CryptoServerCXI.KeyAttributes attr = cxi.getKeyAttributes(key, true);
          Nombre=       attr.getName();
          Grupo =       attr.getGroup();
          llave = key;
          Label = attr.getLabel();
      } catch (CryptoServerException ex) {
          Logger.getLogger(Llaves.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(Llaves.class.getName()).log(Level.SEVERE, null, ex);
      }
     

  }

    /**
     * @return the Modulus
     */
    public byte[] getModulus() {
        return Modulus;
    }

    /**
     * @param Modulus the Modulus to set
     */
    public void setModulus(byte[] Modulus) {
        this.Modulus = Modulus;
    }

    /**
     * @return the Nombre
     */
    public String getNombre() {
        return Nombre;
    }

    /**
     * @param Nombre the Nombre to set
     */
    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    /**
     * @return the Grupo
     */
    public String getGrupo() {
        return Grupo;
    }

    /**
     * @param Grupo the Grupo to set
     */
    public void setGrupo(String Grupo) {
        this.Grupo = Grupo;
    }
  
public String toString(){
    if(Nombre != null)
            return Nombre + " - " + Grupo;
    else
        return "PKI- " + Label + " - " + Grupo;
        }

    /**
     * @return the llave
     */
    public CryptoServerCXI.CryptoServerCXI.Key getLlave() {
        return llave;
    }

    /**
     * @param llave the llave to set
     */
    public void setLlave(CryptoServerCXI.CryptoServerCXI.Key llave) {
        this.llave = llave;
    }
  
  
}
