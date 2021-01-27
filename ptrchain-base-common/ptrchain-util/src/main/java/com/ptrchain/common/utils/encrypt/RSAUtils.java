package com.ptrchain.common.utils.encrypt;


import com.ptrchain.common.exception.GlobalException;
import com.ptrchain.common.result.BasicCodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


/**
 * @author : gelinghu
 * @Description: RSA工具类，支持长度为2048的秘钥
 * @date : 2019/6/8
 * @time : 16:29
 */
@Slf4j
public class RSAUtils {


    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";


    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";


    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;


    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;


    /**
     * @param keySize 生成的秘钥长度  一般为1024或2048
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair(int keySize){
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error(String.format("生密钥异常%s", ExceptionUtils.getStackTrace(e)));
            throw new GlobalException(BasicCodeMsg.SERVER_ERROR);
        }
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);

        log.info("publicKey：" + Base64.encode(publicKey.getEncoded()));
        log.info("privateKey：" + Base64.encode(privateKey.getEncoded()));

        return keyMap;
    }


    /**
     * 对已加密数据进行签名
     *
     * @param data       已加密的数据
     * @param privateKey 私钥
     * @return 对已加密数据生成的签名
     * @throws Exception
     */

    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encode(signature.sign());
    }


    /**
     * 验签
     *
     * @param data      签名之前的数据
     * @param publicKey 公钥
     * @param sign      签名之后的数据
     * @return 验签是否成功
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decode(sign));
    }


    /**
     * 用私钥对数据进行解密
     *
     * @param encryptedData 使用公钥加密过的数据
     * @param privateKey    私钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        //Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateK);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();


        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 使用私钥加密过的数据
     * @param publicKey     公钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }


    /**
     * 公钥加密
     *
     * @param data      需要加密的数据
     * @param publicKey 公钥
     * @return 使用公钥加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 私钥加密
     *
     * @param data       待加密的数据
     * @param privateKey 私钥
     * @return 使用私钥加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 获取私钥
     *
     * @param keyMap 生成的秘钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap){
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encode(key.getEncoded());
    }


    /**
     * 获取公钥
     *
     * @param keyMap 生成的秘钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encode(key.getEncoded());
    }

    public static void main(String[] args) throws Exception {

//        String data = "POST\n" +
//                "dnJAH2ZoI0gynIasbx/hBg==\n" +
//                "application/json;charset=UTF-8\n" +
//                "1579071882.5693865\n" +
//                "/robot/wx192djsjejd/groups";
        String sig = "fuSpJtPPSvVO59w4PUFv2j7jIUc0M3SVywC/NZ/LTgkAt3AzK0eNJM2cpMa2U98rNI2OWrHvEJhcmjzR9TfV0YWWWVht9gVvBUYuyRCuVXLdjv1uFZ5FY2LMXWnR6n253vZOITldtj9MyODEAVmVbbv7UN1HTXmms9ttHWXKSEpDIyx8D7jOWABFpmW+MsV9//+NuyT9AEYQHIvtHyTZn4GLC0FKYFCoIYStFMbLbuI6l/UK/XaOoQnUkM9sx44oXnhxiB40T+S3MNQ3pafcfNzZ5W5xLQed/7v6dxjEODB4krKfZkgFs1l3JoAlVDFpSm5FbQKgwn8eOtj/84xJQg==";
        /*RSA  1024 */
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIarYvrIMZGHKa8f2E6ubg0//28R1zJ4ArD+XELXYvDrM8UBR42PqJCpjPN3hC91YAnnk2Y9U+X5o/rGxH5ZTZzYy+rkAmZFJa1fK2mWDxPYJoxH+DGHQc+h8t83BMB4pKqVPhcJVF6Ie+qpD5RFUU/e5iEz8ZZFDroVE3ubKaKwIDAQAB";
//        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIhqti+sgxkYcprx/YTq5uDT//bxHXMngCsP5cQtdi8OszxQFHjY+okKmM83eEL3VgCeeTZj1T5fmj+sbEfllNnNjL6uQCZkUlrV8raZYPE9gmjEf4MYdBz6Hy3zcEwHikqpU+FwlUXoh76qkPlEVRT97mITPxlkUOuhUTe5sporAgMBAAECgYA0aSND37iifKUTaKOpXIKFoI23910EMAnrAXmaTIkafUBZjL7Ay0Q+QIcDHeGjgNlW9YvGXMbB5wMhMYKMgOUV1FpeqQdDslO4Z7zynRjkDJkjOKkE2/j10CvmNO8e2uCWKsYYUE9IyTkxcypjBCv16ifT0qmdxb7uKLccYI16eQJBANMutfNO/q7kUKiYvilBLN9+pZOg6eTmKmV0Xygoa3ClpQTfurwLA8W/Fv3oXnjHXTryNVHeoxSH69imo0RZ9kcCQQClXhMbXlfvl5iInmwziFhtYBztvkLuyQ084FgszR7iR0nuOWoURLQa5O7sLL724FNRlSvOCmmmWguh2vmQgRr9AkBDS5tHkWCvMqpRT3spgk9eWOlChgCCpKXV9qNsFJVILEDNsM28pnXpSd91wdp4+m7HHe/Hyv6EyFtrio50dYZ5AkAODVVwUO8GBArJKTUml+JzwOQUa8OCSQFf9+xmOjPypH4qySQzfrcTRfrrhM3haqSJ3TQwuP/LTAGLCnGEjwP9AkBqFFyrrQviPOhwel3NWjRv8mftOFgnm0Isk/NQJ4JtoahYvPDeUyP80WSuVWnPyV4zHz9Kw7BggYCPc4xZDACV";


        /*RSA 2048*/

//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApXrK+lsS3nUyqWnXeULYdKJ6rNLNsTUBCGGSicvgjB8AYObZtjEsvRgFqn72mZF6uieTsih588BWGiVMeUv9AaXA1dKD63Dl98RMWu++KtlNFOA+VxZFTMeOQ02VqNruMOE8gzFxwG7HXqgO/6nPRupjhj1LCoHHsVTH0VShbeqYQ6xHgNJxc99qFpEU38hgfnr4ydlz00DuAjUbIGC6wJf6oV0EScnXE6rSrCDDsIDledVF1XO+OTa9NTUVgKDecGt8gHwk8xV1G2Z9u6rdT61lrCcreKCjvcfhbmyswVd4th7NVCwFyyzDQMcR/A1yu9C56tBc1Xi8kURryywsswIDAQAB";
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQClesr6WxLedTKpadd5Qth0onqs0s2xNQEIYZKJy+CMHwBg5tm2MSy9GAWqfvaZkXq6J5OyKHnzwFYaJUx5S/0BpcDV0oPrcOX3xExa774q2U0U4D5XFkVMx45DTZWo2u4w4TyDMXHAbsdeqA7/qc9G6mOGPUsKgcexVMfRVKFt6phDrEeA0nFz32oWkRTfyGB+evjJ2XPTQO4CNRsgYLrAl/qhXQRJydcTqtKsIMOwgOV51UXVc745Nr01NRWAoN5wa3yAfCTzFXUbZn27qt1PrWWsJyt4oKO9x+FubKzBV3i2Hs1ULAXLLMNAxxH8DXK70Lnq0FzVeLyRRGvLLCyzAgMBAAECggEAcMFq7YyBHvPcwXNNT2Ms+sx7qqc5YTYsrflOUHaLWu8E4wJp9qsrQO3Nbo90mM94QMAW856A+oZkJQylP0F39FdtPkVjflo/WMR5Ar25ZpkK3N6vm8ehEOApH9MN1zDWfJf9E6ftSqIxV15MWgjrI/cyOZYqO86Ss1KBlTGFjc7jhBIR/2m/QHql1vLCH1T6mdVGQp4Uz9pdhESYy2ZuKm7XZIWmCrZARY5ap/so1PgIOifztcdtqs4R+T1FQedbjY4zSCM6URWmORH8mz0oQAQh23O+/9m/totdzSwAsp6S39vEY09oWSblIhwRHEXjhWRW2AOqLx5YQvkNyWdTgQKBgQDXu6DCGUfTnLkenzPWDLImFPL9zjiEoASy7XOLjDV/+tSM+SulgPBVeC6TSmGCdSlOvl8G7M3Ax+XYGo9wt9BBcpvQB8uGup8ihDZ7ARHsehHEr4OtCRWpi3ahLwk6sa9VuFja07/59MKhXWB30k4ICQ5l1b52bkVcn76lyiJvXQKBgQDEXel0+jtIkh3pZ6FYCVirsoswsAYQDIhgVB/2CKl6VPX1HPlERkG7mcAfJo1aRYS7l7BRoiQXBUpPL6fyBTA0H5nrRADcqNLUYvMqXor6ksMmoVd8rB80cHE8zg3JwbIx3Abc87JUzOEy/SsOJTaRENJ+S8S/fxdy1La/2DEbTwKBgQCp6wPEvarANwnr35MxfhB3RDrbw6jqdSseTz6Z5FZjNgph+wPwuK0TdLSimIxS97sQZYttZejtdcVIQCtIqc7jLcUhxnH87VLYGfDbU01tx/vkwiVhje08/Db035pSW49xYeDCgtQLqUf/Vw/bcI67b36ioQcCOvpw6PV9mccjlQKBgAFHh1zewIIhiZZauX9kWDKOMD/59aAnJMBT6/1jYw9L/GpDbRBX0NfTEaBiVOtwpoO+YkS15LxNqYVIjzSRehXs2sjgoYzOgMknd+Qfx5IIYMMgq/xA9tcVoHc/9HELFQkkINtRryjESPdXFO+7qFZboLN39Ok2f8WcwzAOzYsbAoGBALm+m1dhdbawwuq3kCjb55suJipWN41U1QU/plqkwSswbOcAoo/Lmtoq12O637qjzxe60hHgpqxNbt45uOx04N+J74Cp0kJQjjPjYVXN096oE4AZvB7kTabTvDu/WVgrjE9y/9dPBl+ZcPY1S9vy65Xcoa2Ew8BC0aCVoU1mmi43";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl60arKaUWJDRp39uTsV3fBJPe2HkjQp8iqdlAhGMrId3vFCPdMfGOxRsRyJzkZbSvlimXHXWQTv/fleNDdvNWzmNcHNze5p/TgATiDLBxGoMR7bWYfLhmPQIaHUtnxIAyR90JMpgMigEsWLRu9gHV7Owc1a3kRIP+5cWRMqyt6kf66faK35C/1vIycfE/d/JmlZtov996SYVhTw+S0Hx8Ww63ojx+MKbLMpdZCzRjB0NNj8d0R4R3sXYre098a1zJl6XGkyCbCi6MYvVXkKEBIxATvjgvag07t5cYKjj2r6Iu4XF80ND4X+9TW3445rMRj/aCyx8ZoD5RwSheNhWkwIDAQAB";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXrRqsppRYkNGnf25OxXd8Ek97YeSNCnyKp2UCEYysh3e8UI90x8Y7FGxHInORltK+WKZcddZBO/9+V40N281bOY1wc3N7mn9OABOIMsHEagxHttZh8uGY9AhodS2fEgDJH3QkymAyKASxYtG72AdXs7BzVreREg/7lxZEyrK3qR/rp9orfkL/W8jJx8T938maVm2i/33pJhWFPD5LQfHxbDreiPH4wpssyl1kLNGMHQ02Px3RHhHexdit7T3xrXMmXpcaTIJsKLoxi9VeQoQEjEBO+OC9qDTu3lxgqOPavoi7hcXzQ0Phf71NbfjjmsxGP9oLLHxmgPlHBKF42FaTAgMBAAECggEBAJaTFekSUjD+S/QxsKtoo667asZ9L2zJXUw01DTSgUoCDl8XxuLTNLsQElIoJH0DMuIelcEyhmoqiwC9H94DOIH4x648Hwg1H26q6rkKUoyQDvLe00NX4Jh8oyA4O/sv3zHLFP01H3yLd+U1yirmUpvBWkmWbBQLz2wf2M6gP/3MzIk1r2LGlAmRuJyRaxPOw/z3QMMZQ9ZzXsHqSNZ2ju+DabxpyGLPW2sGaAB+1pk4zDM/RaXA3y2Q0/YUv14A5+yLQTTatknRWpuD0KgZg88sIQ843cfo03L5+C6gaw/1kRIy+tz26xqez8PsVcvfmYBP9erPD5EifZLzMY6VVukCgYEA77MvihYXM8G70R/z9iNESxIf+98cWkZWPv60VZgU+zb3BR03jQIW450wyWqJ6gJqytZXOqwtiUWqhAH+YQfUoBoG9Simx2D3riavdrPk7E02OYUcGi8QeWPXGLbijC/fquiNeuTdXsPc94cY2HVIYRLV5SqSy5w9LUmYAUjfAP0CgYEAof2OyC3GUSzRBYA2kHKWpb48cSpsXVEfDk29fl8jpAfxol8GfzG/lzUv/wtmBXowFmxPbl8/M6IE3HP2TUSQ4KAVlaS3ERFGsciE2ht+U4soHV5oeMF1ga3ceh017gZ6NgfeZPB6mXa6KcosJq62f4U0QQEU+y0XG1xAz2HD0s8CgYEAzP2Rx2by+slRBRxtd+9bJVRf0/hPeAi1E3Sh5p2EO/8aq937tvS1fHzezzH0thLRH5zExqGe48mXdQN7d6HJvg3kK19TmikcSGw/HKXmPgweNnzWtrHPRfETb8hf4IHEp5XwAMyfYL7iRzHbFqredl40XKLXHZgFXaiVfWvDg0UCgYBZPV5TWSRPw+jCpwdoPDhRxqN1lHFuVypMrvcdjzwLOXUchk0F9Voe6uYuExF5Iv8q2zBHFaGzyCX1bMzbdFJ77LT3Y26WsHXJqzXFEjBdxeOrij7zh2A8uJMlksILBvZ8nYkfAnwH3yhfrUj1ZnqTdcZ7qSTt2sFICWKK3MkMcQKBgB01QhxqJUs80Nl7f7a5aKICXN89BU6A5UpNBh0zQWbFsd59HW8rVCKSLy12zlsgxLDtT/vjT2ye9YO01BKtBYHgwH+IptkNphbEq0VmnQaMB/hpYsgqIxEdBsxngA3P09GL09KQgkpYtqrqUesiBzY+l+kJ1aI8J/ElMdtoIBy5";
//        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQClesr6WxLedTKpadd5Qth0onqs0s2xNQEIYZKJy+CMHwBg5tm2MSy9GAWqfvaZkXq6J5OyKHnzwFYaJUx5S/0BpcDV0oPrcOX3xExa774q2U0U4D5XFkVMx45DTZWo2u4w4TyDMXHAbsdeqA7/qc9G6mOGPUsKgcexVMfRVKFt6phDrEeA0nFz32oWkRTfyGB+evjJ2XPTQO4CNRsgYLrAl/qhXQRJydcTqtKsIMOwgOV51UXVc745Nr01NRWAoN5wa3yAfCTzFXUbZn27qt1PrWWsJyt4oKO9x+FubKzBV3i2Hs1ULAXLLMNAxxH8DXK70Lnq0FzVeLyRRGvLLCyzAgMBAAECggEAcMFq7YyBHvPcwXNNT2Ms+sx7qqc5YTYsrflOUHaLWu8E4wJp9qsrQO3Nbo90mM94QMAW856A+oZkJQylP0F39FdtPkVjflo/WMR5Ar25ZpkK3N6vm8ehEOApH9MN1zDWfJf9E6ftSqIxV15MWgjrI/cyOZYqO86Ss1KBlTGFjc7jhBIR/2m/QHql1vLCH1T6mdVGQp4Uz9pdhESYy2ZuKm7XZIWmCrZARY5ap/so1PgIOifztcdtqs4R+T1FQedbjY4zSCM6URWmORH8mz0oQAQh23O+/9m/totdzSwAsp6S39vEY09oWSblIhwRHEXjhWRW2AOqLx5YQvkNyWdTgQKBgQDXu6DCGUfTnLkenzPWDLImFPL9zjiEoASy7XOLjDV/+tSM+SulgPBVeC6TSmGCdSlOvl8G7M3Ax+XYGo9wt9BBcpvQB8uGup8ihDZ7ARHsehHEr4OtCRWpi3ahLwk6sa9VuFja07/59MKhXWB30k4ICQ5l1b52bkVcn76lyiJvXQKBgQDEXel0+jtIkh3pZ6FYCVirsoswsAYQDIhgVB/2CKl6VPX1HPlERkG7mcAfJo1aRYS7l7BRoiQXBUpPL6fyBTA0H5nrRADcqNLUYvMqXor6ksMmoVd8rB80cHE8zg3JwbIx3Abc87JUzOEy/SsOJTaRENJ+S8S/fxdy1La/2DEbTwKBgQCp6wPEvarANwnr35MxfhB3RDrbw6jqdSseTz6Z5FZjNgph+wPwuK0TdLSimIxS97sQZYttZejtdcVIQCtIqc7jLcUhxnH87VLYGfDbU01tx/vkwiVhje08/Db035pSW49xYeDCgtQLqUf/Vw/bcI67b36ioQcCOvpw6PV9mccjlQKBgAFHh1zewIIhiZZauX9kWDKOMD/59aAnJMBT6/1jYw9L/GpDbRBX0NfTEaBiVOtwpoO+YkS15LxNqYVIjzSRehXs2sjgoYzOgMknd+Qfx5IIYMMgq/xA9tcVoHc/9HELFQkkINtRryjESPdXFO+7qFZboLN39Ok2f8WcwzAOzYsbAoGBALm+m1dhdbawwuq3kCjb55suJipWN41U1QU/plqkwSswbOcAoo/Lmtoq12O637qjzxe60hHgpqxNbt45uOx04N+J74Cp0kJQjjPjYVXN096oE4AZvB7kTabTvDu/WVgrjE9y/9dPBl+ZcPY1S9vy65Xcoa2Ew8BC0aCVoU1mmi43";
//
//        try {
//
            String data = "的房间诶";
//
            byte[] publicEncryptBytes = RSAUtils.encryptByPublicKey(data.getBytes(), publicKey);
            log.info("公钥加密后的数据：" + Base64.encode(publicEncryptBytes));
            byte[] privatDecryptBytes = RSAUtils.decryptByPrivateKey(publicEncryptBytes, privateKey);
            log.info("私钥解密后的数据：" + new String(privatDecryptBytes));
//
//
//            log.info("--------------------");




////
//            byte[] privateKeyEncryptBytes = RSAUtils.encryptByPrivateKey(data.getBytes(), privateKey);
//            log.info("私钥加密后的数据：" + Base64.encode(privateKeyEncryptBytes));
//
//            String singnData = RSAUtils.sign(data.getBytes(), privateKey);
//        String singnData = "Mxo7xs0D9WAsWmr0BcQOhOO5QdYo4vAsP6VHqQ2BTxuRt1pZ+7cASQYCjLWwnj2KN44SzyZs7S5v5+Ozn4qczpiPgsp7tUco2Uw6x2fh3HslF7n9u/jBrYErMTzdplBiXyRYpIIzrm3w9CW0cRkui2zFGSZyGm6xGYx7uHaLESGwMIJASQftBgOc/AvQniGfX9GouMoIHXj15oL04s2N8rCUsuhX/thoLouoyWaiBHfvRP23goe2cQvNxzi0T6HQeFjzj8Cc2YV6ZIk12Z9CPp7H6xJvGoOyaLJNvD/8iJ5Q8RKFPT65bfIPdb7MJXsiCjGTSsSp9l0D+NwT9wC70Q==";
//            log.info("私钥签名后的数据：" + singnData);
//
//            byte[] publicDecryptBytes = RSAUtils.decryptByPublicKey(singnData.getBytes(), publicKey);
//            log.info("公钥解密后的数据：" + new String(publicDecryptBytes));
//
//            boolean isSign = RSAUtils.verify(data.getBytes(), publicKey, singnData);
//            log.info("签名是否正确：" + isSign);
//
//
//           byte[] privateKeyEncryptBytes = RSAUtils.encryptByPrivateKey(data.getBytes(), privateKey);
//            log.info("私钥加密后的数据：" + Base64.encode(privateKeyEncryptBytes));
//
//            String singnData = RSAUtils.sign(data.getBytes(), privateKey);
//            log.info("私钥签名后的数据：" + singnData);
//            log.info(sig);
//
//
//            byte[] publicDecryptBytes = RSAUtils.decryptByPublicKey(privateKeyEncryptBytes, publicKey);
//            log.info("公钥解密后的数据：" + new String(publicDecryptBytes));
//
//            boolean isSign = RSAUtils.verify(data.getBytes(), publicKey, sig);
//            log.info("签名是否正确：" + isSign);

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Map<String, Object> stringObjectMap = genKeyPair(2048);
//        System.out.println(stringObjectMap);
//        String privateKey1 = getPrivateKey(stringObjectMap);
//        String publicKey1 = getPublicKey(stringObjectMap);
//        JSONObject jb = new JSONObject();
//        JSONObject jbdata = new JSONObject();
//        jbdata.put("expiresIn", "7200");
//        jbdata.put("accessToken", "ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SmxlSEFpT2pFMU5qQTBNVFF4TnpVeU1qZ3NJbkJoZVd4dllXUWlPaUo3WENKMWMyVnlTV1JjSWpwY0lqRXdNREF3TURBeFhDSXNYQ0poY0hCSlpGd2lPbHdpV2tkU1V6SXdNREF3TVZ3aUxGd2lkWE5sY2s1aGJXVmNJanB1ZFd4c0xGd2ljbVZoYkU1aGJXVmNJanB1ZFd4c0xGd2liVzlpYVd4bFhDSTZiblZzYkN4Y0ltVnRZV2xzWENJNmJuVnNiQ3hjSW14aGMzUk1iMmRwYmx3aU9tNTFiR3dzWENKM2FHOWNJanBjSWxOQlFWTmZRVlZVU0U5U1NWUlpYQ0lzWENKMGIxd2lPbHdpVTBGQlUxOVFURUZVUms5U1RWd2lMRndpWVhWMGFGVnpaWEpVZVhCbFhDSTZYQ0pWVTBWU1hDSXNYQ0p0ZFd4MGFYQnNaVndpT21aaGJITmxMRndpZEdsdFpVeHBibVZjSWpwdWRXeHNMRndpY21WemIzVnlZMlZ6WENJNmUxd2ljSEp2WkhWamRGd2lPbHdpWENJc1hDSmhkWFJvWENJNlhDSmNJaXhjSW1OdmJYQmhibmx6WENJNlhDSmNJaXhjSW5KbGNHOXlkRndpT2x3aVhDSXNYQ0p3YkdGdVhDSTZYQ0pjSWl4Y0luQnliMlIxWTNSelhDSTZYQ0pjSW4xOUluMC5rU1pRYjdFakd6Ync4LWNTSENtNk0wWFJPUnFBODFmU3ktWTB0SmZxT3NV");
//        jb.put("data", jbdata);
//        byte[] bytes = encryptByPublicKey(JSON.toJSONBytes(jb.get("data")), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzQQQHnWslT/9cDKN5/cGrj8lavAncdTgFyYg9JBL9G7hwfT4DLPI/HPX+bOwfUP1F0pHnCRG1X+7rK95NK0+aPxVcLqusTduoPIKl9+kFPl6eHbxiDUos7jt2W+y4fibhHhL2UmP4B2gmI+PcMKf/2B9vfz2g3MPF2Rw5TvTvYMTEkzRcvjvt6rRSYBcSN5+KaqB6YLcZEEND2TvK3NFBmKArKkVXZYowJugGVzQOl/19G6aO8Vxg8Z0R914VdEzAfMM/XoGdSs2PVrHFuBTmHrygL1KOFoXelStY69VtzQCVunzuwGrcimoPSK2CYO78G0rIqRT5x6mVzPMEJhPsQIDAQAB");
//        System.out.println(JSON.toJSONString(jb));
//        String encode = Base64.encode(bytes);
//        System.out.println(encode);
//        byte[] bytes1 = RSAUtils.decryptByPrivateKey(Base64.decode(encode), "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDNBBAedayVP/1wMo3n9wauPyVq8Cdx1OAXJiD0kEv0buHB9PgMs8j8c9f5s7B9Q/UXSkecJEbVf7usr3k0rT5o/FVwuq6xN26g8gqX36QU+Xp4dvGINSizuO3Zb7Lh+JuEeEvZSY/gHaCYj49wwp//YH29/PaDcw8XZHDlO9O9gxMSTNFy+O+3qtFJgFxI3n4pqoHpgtxkQQ0PZO8rc0UGYoCsqRVdlijAm6AZXNA6X/X0bpo7xXGDxnRH3XhV0TMB8wz9egZ1KzY9WscW4FOYevKAvUo4Whd6VK1jr1W3NAJW6fO7AatyKag9IrYJg7vwbSsipFPnHqZXM8wQmE+xAgMBAAECggEADO9vsOxNylDyRaz65SLh6gaT7s5ePsFmri86rxOXp0GDvwAc0nhZeww4GUosHYs3IJQQNprXWUso5TlhFWDE6kzZezZcJVRRB5id8H0s1iHzhzxB+w0Xl1dYsnpot/c+oLoSVxBcIpDb/CQ6saul+q/03xLh3uNYlWpB8US4FGHe0d4OM5XsSjC1VY7PwhFgHds+AaJ6V25AU76Npp8LBgvQouqEj7xyt52BphbcC6NIwt9mo2xdxoyKFzLfDz6+yw2aeDRjfjcWvzmie15HlhbhNMJyaNsV2NpoAd/quuDdrgWBkTvg9tY6/w+PJfFw84Ab/13pW23VVc+j60vlrQKBgQDyzWP/PoHKnjBcBx917IAu629SDFnHbPMetBAUc75TFPRnbB/8TjVV/Iyovg7jd82UN7VBQxZ8mP+dmePJYT8KqYRdFlcGTk5T9GgoxXbtP3dQM2d0cgmaH9hSEcCvrw6dXJBdjhec7ErSfsPP0VONfcgmFnnYACDqdWjYdlnr+wKBgQDYKN8op3zJV1O7Q3vfjuENmoR/XVjg19EyRdsdIwRGQnsel9HgBla0LZof7t4bKYIyzJ/+l8sNQNAV6Ay8YfqZKKzAQlb2VSLIgr+fTvM3JigklYtgjy7z2p/2wo1ZhelOgum9YYK3PygV3WpW/UZaGS4e5YoXRtEcbCoQFdQXQwKBgAFBhLnmRd+Zb+CZ/NSsU4FqoFnJTJGWg69t+QNSEfL2EcmTKpswgGAgfU3GJeeRUBPrMdnTkgM0+2WCTUZWZXAVrmSY3/xdXVRb2EoVs2bIxEI68dcNW/VbItBR3TBLdITl3K1PNdoO8tgh5btmdsstkmJ4DUtLvN/ZmaA9BHwPAoGAIcEwJ+8Po3lJkAKXw29SYYdxjVoMWQAaoNkbwE8Jidcz1rEqiusyeuxsFvR8Jo2y0S/3BBC8GpO2atVTt88tDOG3KEazwb/FsL4KIlx3ynLg7eWUYD27vUS0mhoDbKlmp2X2Dia9/NOV9OQgiPkAFW29MAH0MHJUkTWq09w0xHUCgYBca/fJPu4Hy2eGkX64wGOl7ahjcDtCQf4+6X2IAZcARtIoe2JM3L0gAhVyK/rMPzXHBB5EPD5e7J2mNjOe3wfvWshs9HKulgWPK9B0XIUb0R6E5rfxalpPyXbrfDoCpVfMd9MrqGSIUdCDbG7JNj0ICeMYPIFlHrWBl0snDi8eQg==");
//        System.out.println(new String(bytes1));


    }
}
