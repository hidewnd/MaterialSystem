package com.lyne.base.bean;

/**
 * @author lyne
 */
public class OuterBody {
    /**
     * 出库记录Id
     */
    private String recordId;
    /**
     * 出库凭证
     */
    private String proof;
    /**
     * 物料二维码
     */
    private String qrcode;

    public OuterBody(String recordId, String proof, String qrcode) {
        this.recordId = recordId;
        this.proof = proof;
        this.qrcode = qrcode;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Override
    public String toString() {
        return "OuterBody{" +
                "recordId='" + recordId + '\'' +
                ", proof='" + proof + '\'' +
                ", qrcode='" + qrcode + '\'' +
                '}';
    }
}
