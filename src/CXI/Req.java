/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CXI;

import CryptoServerAPI.CryptoServer;
import CryptoServerAPI.CryptoServerException;
import CryptoServerAPI.CryptoServerUtil;
import CryptoServerCXI.CryptoServerCXI;
import Log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;



/**
 *
 * @author dotNet
 */
public class Req {

    private  String RFC_SAT = "SAT970701NN3";
    private  String RFC_REP_SAT = "GASM690101H29";
    private  String SERIAL_NUMBER = " / GASM690101HDFRNN09";
    private  String ORGANISATION_NAME = "SERVICIO DE ADMINISTRACION TRIBUTARIA";
    private  int mech;
    private Log log = new Log();

    
    public boolean ObtenerDatosDeConfiguracion()
    {
        try {
            DocumentBuilderFactory dbFactory ;
            DocumentBuilder dBuilder ;
            Document doc ;
            String ruta2="";
            log.Write("Obteniendo los datos de confguracion");
            ruta2= System.getProperty("user.dir") +"/config.xml";
            
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(ruta2);
            
            RFC_SAT = (doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("rfcSat").getNodeValue());
            RFC_REP_SAT= (doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("rfcRepresentante").getNodeValue());
            SERIAL_NUMBER=(doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("curpRepresentante").getNodeValue());
            ORGANISATION_NAME= (doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("nombreSat").getNodeValue());
            
            log.Write(RFC_SAT);
            log.Write(RFC_REP_SAT);
            log.Write(SERIAL_NUMBER);
            log.Write(ORGANISATION_NAME);
            return true;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Req.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SAXException ex) {
            Logger.getLogger(Req.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Req.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        catch (Exception ex) {
            Logger.getLogger(Req.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private static PublicKey loadPublicFromModulus(String hexMod) throws Exception {
        BigInteger publicExponent = BigInteger.valueOf(65537L);
        BigInteger modulus = new BigInteger(Hex.decode(hexMod));
        return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
    }

   

    public  String GenerarReq(
            String modulus,
            String rfcPac,
            String noPac,
            String nombreLlave,
            String grupo,
            String rutaSalida,
            CXI cxi,
            Llaves llave) throws Exception
    {
        String mensaje="";
        String pubKey = null;
        String _mod = modulus.toUpperCase();
        PublicKey pub = null;
        
       try
       {
           if(! ObtenerDatosDeConfiguracion())
               return "No se pudieron obtener los datos de configuracion";
        pub = loadPublicFromModulus(_mod);

        String password = "AKvCf+5nwGvGW2dd/7BwDk1uoow=";
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        BigInteger exponent = BigInteger.valueOf(65537L);


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pub.getEncoded());
        RSAPublicKey pubKeyRSA;
        pubKeyRSA = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        RSAPublicKeySpec pKey2 = new RSAPublicKeySpec(pubKeyRSA.getModulus(), exponent);
        pub = keyFactory.generatePublic(pKey2);

        log.Write("Ruta de salida: " + rutaSalida);
        File output = new File(rutaSalida);
        FileOutputStream fos = new FileOutputStream(output);
        ASN1OutputStream asno = new ASN1OutputStream(fos);


        ASN1Set x500UniqueIdentifier = new DERSet(new DERSequence(new ASN1Encodable[]{RFC4519Style.x500UniqueIdentifier, new DERPrintableString(RFC_SAT + " / " + RFC_REP_SAT)}));
        ASN1Set serialNumber = new DERSet(new DERSequence(new ASN1Encodable[]{RFC4519Style.serialNumber, new DERPrintableString(" / " + SERIAL_NUMBER)}));
        ASN1Set organizationName = new DERSet(new DERSequence(new ASN1Encodable[]{org.bouncycastle.asn1.x500.style.BCStrictStyle.O, new DERPrintableString(ORGANISATION_NAME)}));
        ASN1Set organizationUnitName = new DERSet(new DERSequence(new ASN1Encodable[]{org.bouncycastle.asn1.x500.style.BCStrictStyle.OU, new DERPrintableString("PAC" + rfcPac + noPac)}));

        DERSequence subjectInfo = new DERSequence(new ASN1Encodable[]{x500UniqueIdentifier, serialNumber, organizationName, organizationUnitName});

        ASN1EncodableVector vInfo = new ASN1EncodableVector();
        vInfo.add(new ASN1Integer(0));
        vInfo.add(subjectInfo);
        vInfo.add(DERSequence.fromByteArray(pub.getEncoded()));
        vInfo.add(new DERTaggedObject(0, new DERSequence(new ASN1Encodable[]{PKCSObjectIdentifiers.pkcs_9_at_challengePassword,
            new DERSet(new DERPrintableString((password)))})));

                    //new DERSet(new DERPrintableString(encodePassword(password)))})));

        DERSequence seqInfo = new DERSequence(vInfo);

        byte[] requestInfo = seqInfo.getEncoded();
        log.Write("SE envia a obtener la firma");
        byte[] sig = cxi.ObtenerFirma(requestInfo,llave);
        if(sig == null)
            return "Ocurrio un error al generar la firma";
        
        log.Write("Se obtuvo la firma correctamente con longitud de: " + sig.length);

        DERSequence seqSigInfo = new DERSequence(new ASN1Encodable[]{PKCSObjectIdentifiers.sha1WithRSAEncryption, DERNull.INSTANCE});
        DERBitString signature = new DERBitString(sig);
        DERSequence seq = new DERSequence(new ASN1Encodable[]{seqInfo, seqSigInfo, signature});

        asno.writeObject(seq);
        fos.close();
        log.Write("Se guardo correcamente el archivo REQ");
       }
       catch(Exception ee)
       {
           log.Write("Ocrurió un error al generar el req" + ee.getMessage());
           mensaje="Ocurrio un error al generar el requerimiento";
       }
        
        return mensaje;

    }

    private  String encodePassword(String password) throws Exception {
        return new String(Base64.encode(MessageDigest.getInstance("SHA1").digest(RFC_SAT.concat(password).getBytes("UTF-8"))));
    }

   

   

}
