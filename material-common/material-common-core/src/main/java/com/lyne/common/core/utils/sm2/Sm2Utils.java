package com.lyne.common.core.utils.sm2;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.Base64;

/**
 * sm2算法工具类
 *
 * @author lyne
 */
public class Sm2Utils {

    /**
     * 生成密钥对
     *
     * @return HashMap<String, String>
     */
    public static keys getKeyStr() {
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        return new keys(
                pair.getPublic().getEncoded(),
                pair.getPrivate().getEncoded());
    }

    /**
     * 通过公钥进行SM2加密
     *
     * @param context   被加密内容
     * @param publicKey 公钥
     * @return 加密文本
     */
    public static String encode(String context, String publicKey) {
        return encode(context, publicKey, null);
    }

    /**
     * 通过密钥对进行SM2加密
     *
     * @param context    被加密内容
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return 加密文本
     */
    public static String encode(String context, String publicKey, String privateKey) {
        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        return encode(context, sm2);
    }

    /**
     * 通过密钥对进行加密
     *
     * @param context 加密内容
     * @param sm2     sm2对象
     * @return 加密文本
     */
    public static String encode(String context, SM2 sm2) {
        return sm2.encryptBcd(context, KeyType.PublicKey);
    }

    /**
     * 根据私钥进行解密
     *
     * @param context    加密文本
     * @param privateKey 私钥
     * @return 解密内容
     */
    public static String decode(String context, String privateKey) {
        return decode(context, null, privateKey);
    }

    /**
     * 根据密钥对进行解密
     *
     * @param context    加密内容
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return 解密内容
     */
    public static String decode(String context, String publicKey, String privateKey) {
        return decode(context, SmUtil.sm2(privateKey, publicKey));
    }

    /**
     * 根据SM2解密
     *
     * @param context 加密内容
     * @param sm2     sm2
     * @return 解密内容
     */
    public static String decode(String context, SM2 sm2) {
        return StrUtil.utf8Str(sm2.decryptFromBcd(context, KeyType.PrivateKey));
    }


    /**
     * 通过Sm2算法加密
     *
     * @param context 被签名的数据数据
     * @param keys    密钥对
     * @return 签名
     */
    public static String sign(String context, keys keys) {
        return sign(context, keys.getPrivate(), keys.getPublic());
    }

    public static String sign(String context, String aPrivate) {
        return sign(context, new SM2(aPrivate, null));
    }

    public static String sign(String context, String aPrivate, String aPublic) {
        return sign(context, new SM2(aPrivate, aPublic));
    }

    public static String sign(String context, SM2 sm2) {
        return sm2.signHex(HexUtil.encodeHexStr(context));
    }

    public static boolean verify(String context, String publicKey, String sign) {
        return new SM2(null, publicKey)
                .verifyHex(HexUtil.encodeHexStr(context), sign);
    }


    /**
     * 密钥对实体
     */
    public static class keys {
        private String id;
        private byte[] publicKey;
        private byte[] privateKey;

        public keys(byte[] publicKey, byte[] privateKey) {
            this.id = IdUtil.objectId();
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPublic() {
            return Base64.getEncoder().encodeToString(publicKey);
        }

        public void setPublicKey(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        public String getPrivate() {
            return Base64.getEncoder().encodeToString(privateKey);
        }

        public void setPrivateKey(byte[] privateKey) {
            this.privateKey = privateKey;
        }

        public SM2 getSM2() {
            return new SM2(getPrivate(), getPublic());
        }

        public boolean verify(String context, String sign) {
            return getSM2().verifyHex(HexUtil.encodeHexStr(context), sign);
        }

        @Override
        public String toString() {
            return "keys{" +
                    "id='" + id + '\'' +
                    ", publicKey=" + Arrays.toString(publicKey) +
                    ", privateKey=" + Arrays.toString(privateKey) +
                    '}';
        }
    }
}
