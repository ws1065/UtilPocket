package com.sailing.gb35114;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @program: demo
 * @description: 生成签名证书
 * @author: wangsw
 * @create: 2022-03-02 20:13
 */
public class GenerCert {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        try {
            generGB35114Cert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generGB35114Cert() throws Exception {
        genGMCACert();
        genCertWithCaSign();
    }
    /**
     * 生成国密CA根证书
     *
     * @throws Exception
     */
    public static void genGMCACert() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");

        g.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));

        //密钥对
        KeyPair p = g.generateKeyPair();

        PrivateKey privKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();

        //私钥
        System.out.println("CA PrivateKey:" + Base64.toBase64String(privKey.getEncoded()));

        X500Principal iss = new X500Principal("CN=SAILING,OU=VCS,C=CN,S=ZJ,L=HZ,O=VCS");

        ContentSigner sigGen = new JcaContentSignerBuilder("SM3withSM2").setProvider("BC").build(privKey);
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                iss,
                BigInteger.valueOf(1),
                new Date(System.currentTimeMillis()),
                addDays(new Date(System.currentTimeMillis()), 30),
                iss,
                pubKey).addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true,
                        new X509KeyUsage(0xfe))
                .addExtension(new ASN1ObjectIdentifier("2.5.29.37"), true,
                        new DERSequence(KeyPurposeId.anyExtendedKeyUsage))
//                .addExtension(new ASN1ObjectIdentifier("2.5.29.17"), true,
//                        new GeneralNames(new GeneralName[]
//                                {
//                                        new GeneralName(GeneralName.rfc822Name, "www.sailing.com.cn"),
//                                        new GeneralName(GeneralName.dNSName, "www.sailing.com.cn")
//                                }))
                ;


        //证书
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(sigGen));

        cert.checkValidity(new Date());

        cert.verify(pubKey);


        ByteArrayInputStream bIn = new ByteArrayInputStream(cert.getEncoded());
        CertificateFactory fact = CertificateFactory.getInstance("X.509", "BC");

        cert = (X509Certificate) fact.generateCertificate(bIn);

        System.out.println("CA Cert:" + Base64.toBase64String(cert.getEncoded()));

        saveFile("CAPrikey", privKey.getEncoded());
        saveFile("CARootCert.cer", cert.getEncoded());
        saveFile("C:\\Users\\wangw\\Desktop\\密\\CAPrikey", privKey.getEncoded());
        saveFile("C:\\Users\\wangw\\Desktop\\密\\CARootCert.cer", cert.getEncoded());
        System.out.println("=============测试生成国密CA根证书=============");
    }
    /**
     * 国密CA根证书签发国密证书
     *
     * @throws Exception
     */
    public static void genCertWithCaSign() throws Exception {
        //获得私钥
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(readFile("CAPrikey"));
        PrivateKey caPrivateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        //获得根证书
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509", "BC");
        Certificate caRootCert = certificateFactory.generateCertificate(new FileInputStream("CARootCert.cer"));

        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "BC");
        g.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));

        KeyPair p = g.generateKeyPair();

        PrivateKey privKey = p.getPrivate();
        PublicKey pubKey = p.getPublic();


        ContentSigner sigGen = new JcaContentSignerBuilder("SM3withSM2").setProvider("BC").build(caPrivateKey);
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                (X509Certificate) caRootCert,
                BigInteger.valueOf(new Random().nextInt()),
                new Date(System.currentTimeMillis() - 50000),
                addDays(new Date(System.currentTimeMillis()), 300),
                new X500Principal("CN=VCS"),
                pubKey).addExtension(new ASN1ObjectIdentifier("2.5.29.15"), true,
                        new X509KeyUsage(X509KeyUsage.digitalSignature | X509KeyUsage.nonRepudiation))
                .addExtension(new ASN1ObjectIdentifier("2.5.29.37"), true,
                        new DERSequence(KeyPurposeId.anyExtendedKeyUsage))
//                .addExtension(new ASN1ObjectIdentifier("2.5.29.17"), true,
//                        new GeneralNames(new GeneralName[]
//                                {
//                                        new GeneralName(GeneralName.rfc822Name, "www.sailing.com.cn"),
//                                        new GeneralName(GeneralName.dNSName, "www.sailing.com.cn")
//                                }))
                ;


        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(sigGen));

        cert.checkValidity(new Date());

        cert.verify(caRootCert.getPublicKey());

        ByteArrayInputStream bIn = new ByteArrayInputStream(cert.getEncoded());
        CertificateFactory fact = CertificateFactory.getInstance("X.509", "BC");

        cert = (X509Certificate) fact.generateCertificate(bIn);

        System.out.println("custCert:" + Base64.toBase64String(cert.getEncoded()));
        System.out.println("custPrivateKey:" + Base64.toBase64String(privKey.getEncoded()));
        int i = new Random().nextInt(10);
        saveFile("C:\\Users\\wangw\\Desktop\\密\\Cert"+i+".cer", cert.getEncoded());
        saveFile("C:\\Users\\wangw\\Desktop\\密\\Cert.cer"+i+".pem", Base64.toBase64String(cert.getEncoded()));
        saveFile("C:\\Users\\wangw\\Desktop\\密\\PrivateKey"+i+"", privKey.getEncoded());
        System.out.println("=============测试国密CA根证书签发国密证书=============");

    }
    public static byte[] readFile(String path) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        return bytes;
    }
    public static void saveFile(String path, byte[] data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveFile(String path, String data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}