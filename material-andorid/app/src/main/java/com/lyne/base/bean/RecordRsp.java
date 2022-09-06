package com.lyne.base.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author lyne
 */
public class RecordRsp {

    /**
     * code : 200
     * msg : 查询出入库记录成功
     * data : {"total":9,"totalPage":1,"page":1,"element":[{"sign":1,"status":-2,"createBy":"system","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":0,"applyApproval":0,"recordId":"1511286574506573825","value":191.62,"number":4,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":3,"1484804092155990021":1},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"username","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1512029582772412418","value":866.48,"number":17,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":12,"1484804092155990021":5},"recordStatus":"已完成","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1515541357224116226","value":200.6,"number":2,"proof":"0429B2F9AE56C879B7DC191C67FF514CB552FA5E51101F8FD8E2965E28EF63388733455A97AC38F8C70F3067B6F4D87DB80D2D9A05C3C37A4A11344FAEDDD9ADAFDB84106A6C95DECAABB32F15385E10A2076954068C3537A7A2C89DC44DF3D2282137E14617BE23EC8673367DD391062BB6FCBB","sm2key":"625bee394206cf7e2ef6b32c","materialMap":{},"materialNumberMap":{"1484804092155990020":2},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1515592637661429761","value":152.7,"number":5,"proof":"04B26B5B5C0A5651507F6C5EA4963FDCFFEBC6F6391A91C422F28094D59E6F8CD070F6DA7B09B15C93B4A16D4CA1DCE12CD93261959564EA4BC00916607347B1D5D624D35249915B8823E7B2C41504C34DC5273DB1C2B376917004271BB0F4AEDD9A3852548A371ABD9B37CE8E87B72EEF1AD3BF","sm2key":"6263f6b055d6c9c7280dbed3","materialMap":{},"materialNumberMap":{"1484804092155990019":5},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":-3,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":2,"applyApproval":0,"recordId":"1515620614726074369","value":61.08,"number":2,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":2},"recordStatus":"审核失败","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517845119091458049","value":1050,"number":19,"proof":"","sm2key":"","materialMap":{},"materialNumberMap":{"1484804092155990019":5,"1498308224414179329":1,"1484804092155990021":6,"1484804092155990018":3,"1514513194528907265":4},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517868625405526018","value":691.08,"number":13,"proof":"04F8E4C269D81F110CB0DA25BC36F2E3538B7F237A10DE022A9AABAC9EE07ADEF55A9B2F743F47C0B078325090FFACCEB0CD64130C4D575E216F5EEE66949C03A43B3304162E970052F5033EAE0E7D2F51AA82FB73104C8E115E1ECD5857D2EAF3D88F746723B1B5BCE2C4A8418E6ACDD2B9CB51","sm2key":"6264134055d655ab2b868846","materialMap":{"1484804092155990019":{"6ce836282d17c0ace8f4f0fbc113b5eff3f861b60425aefad2ec4fbdd6c8041a618791923587f2019a24b14a4f8d433174577ba51ed5200c2d051448c8e26e62ec6aa6d3fda6158d43bbff570bf1b378c2707f4cae71394beccd11c9be260d4b":0},"1484804092155990021":{"115ebceacf8060b46a1dee911c9c84cae4e18bcfc1763cccd24037f195c805bbe675dfa2bd659ceeedca415c875181c2dbc5a2070275baaa2bd9048b23214f8ea554784bfd02f29fbe4fafc7a084e7df70fba15afc2bd614001e18ed43cbd29d":0},"1514513194528907265":{"115ebceacf8060b46a1dee911c9c84ca4ca765977862c7cd3c577cc86c9d594d64c9884729b0d4e5b971924aa40758cd6f4d6d839c2701d12b15f54b00edb044120d16230cf4fdc01cf0b771f9d81e48c6a86174da478d518ec2405b18bdef1f":0}},"materialNumberMap":{"1484804092155990019":2,"1484804092155990021":1,"1514513194528907265":10},"recordStatus":"已完成","materialTotalNumber":{"1484804092155990019":0,"1484804092155990021":0,"1514513194528907265":0}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":0,"applyApproval":0,"recordId":"1517868724756004865","value":791.38,"number":14,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":2,"1484804092155990021":1,"1514513194528907265":10,"1484804092155990020":1},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517881173982953473","value":100,"number":1,"proof":"","sm2key":"","materialMap":{"1484804092155990021":{"115ebceacf8060b46a1dee911c9c84cae4e18bcfc1763cccd24037f195c805bbe675dfa2bd659ceeedca415c875181c2dbc5a2070275baaa2bd9048b23214f8ea554784bfd02f29fbe4fafc7a084e7df70fba15afc2bd614001e18ed43cbd29d":0}},"materialNumberMap":{"1484804092155990021":1},"recordStatus":"已完成","materialTotalNumber":{"1484804092155990021":0}}]}
     */

    private int code;
    private String msg;
    /**
     * total : 9
     * totalPage : 1
     * page : 1
     * element : [{"sign":1,"status":-2,"createBy":"system","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":0,"applyApproval":0,"recordId":"1511286574506573825","value":191.62,"number":4,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":3,"1484804092155990021":1},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"username","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1512029582772412418","value":866.48,"number":17,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":12,"1484804092155990021":5},"recordStatus":"已完成","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1515541357224116226","value":200.6,"number":2,"proof":"0429B2F9AE56C879B7DC191C67FF514CB552FA5E51101F8FD8E2965E28EF63388733455A97AC38F8C70F3067B6F4D87DB80D2D9A05C3C37A4A11344FAEDDD9ADAFDB84106A6C95DECAABB32F15385E10A2076954068C3537A7A2C89DC44DF3D2282137E14617BE23EC8673367DD391062BB6FCBB","sm2key":"625bee394206cf7e2ef6b32c","materialMap":{},"materialNumberMap":{"1484804092155990020":2},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1515592637661429761","value":152.7,"number":5,"proof":"04B26B5B5C0A5651507F6C5EA4963FDCFFEBC6F6391A91C422F28094D59E6F8CD070F6DA7B09B15C93B4A16D4CA1DCE12CD93261959564EA4BC00916607347B1D5D624D35249915B8823E7B2C41504C34DC5273DB1C2B376917004271BB0F4AEDD9A3852548A371ABD9B37CE8E87B72EEF1AD3BF","sm2key":"6263f6b055d6c9c7280dbed3","materialMap":{},"materialNumberMap":{"1484804092155990019":5},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":-3,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":2,"applyApproval":0,"recordId":"1515620614726074369","value":61.08,"number":2,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":2},"recordStatus":"审核失败","materialTotalNumber":{}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517845119091458049","value":1050,"number":19,"proof":"","sm2key":"","materialMap":{},"materialNumberMap":{"1484804092155990019":5,"1498308224414179329":1,"1484804092155990021":6,"1484804092155990018":3,"1514513194528907265":4},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517868625405526018","value":691.08,"number":13,"proof":"04F8E4C269D81F110CB0DA25BC36F2E3538B7F237A10DE022A9AABAC9EE07ADEF55A9B2F743F47C0B078325090FFACCEB0CD64130C4D575E216F5EEE66949C03A43B3304162E970052F5033EAE0E7D2F51AA82FB73104C8E115E1ECD5857D2EAF3D88F746723B1B5BCE2C4A8418E6ACDD2B9CB51","sm2key":"6264134055d655ab2b868846","materialMap":{"1484804092155990019":{"6ce836282d17c0ace8f4f0fbc113b5eff3f861b60425aefad2ec4fbdd6c8041a618791923587f2019a24b14a4f8d433174577ba51ed5200c2d051448c8e26e62ec6aa6d3fda6158d43bbff570bf1b378c2707f4cae71394beccd11c9be260d4b":0},"1484804092155990021":{"115ebceacf8060b46a1dee911c9c84cae4e18bcfc1763cccd24037f195c805bbe675dfa2bd659ceeedca415c875181c2dbc5a2070275baaa2bd9048b23214f8ea554784bfd02f29fbe4fafc7a084e7df70fba15afc2bd614001e18ed43cbd29d":0},"1514513194528907265":{"115ebceacf8060b46a1dee911c9c84ca4ca765977862c7cd3c577cc86c9d594d64c9884729b0d4e5b971924aa40758cd6f4d6d839c2701d12b15f54b00edb044120d16230cf4fdc01cf0b771f9d81e48c6a86174da478d518ec2405b18bdef1f":0}},"materialNumberMap":{"1484804092155990019":2,"1484804092155990021":1,"1514513194528907265":10},"recordStatus":"已完成","materialTotalNumber":{"1484804092155990019":0,"1484804092155990021":0,"1514513194528907265":0}},{"sign":1,"status":-2,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":0,"applyApproval":0,"recordId":"1517868724756004865","value":791.38,"number":14,"proof":null,"sm2key":null,"materialMap":{},"materialNumberMap":{"1484804092155990019":2,"1484804092155990021":1,"1514513194528907265":10,"1484804092155990020":1},"recordStatus":"已关闭","materialTotalNumber":{}},{"sign":1,"status":4,"createBy":"root","updateBy":"root","createDate":null,"updateDate":null,"executeApproval":1,"applyApproval":1,"recordId":"1517881173982953473","value":100,"number":1,"proof":"","sm2key":"","materialMap":{"1484804092155990021":{"115ebceacf8060b46a1dee911c9c84cae4e18bcfc1763cccd24037f195c805bbe675dfa2bd659ceeedca415c875181c2dbc5a2070275baaa2bd9048b23214f8ea554784bfd02f29fbe4fafc7a084e7df70fba15afc2bd614001e18ed43cbd29d":0}},"materialNumberMap":{"1484804092155990021":1},"recordStatus":"已完成","materialTotalNumber":{"1484804092155990021":0}}]
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int total;
        private int totalPage;
        private int page;
        /**
         * sign : 1
         * status : -2
         * createBy : system
         * updateBy : root
         * createDate : null
         * updateDate : null
         * executeApproval : 0
         * applyApproval : 0
         * recordId : 1511286574506573825
         * value : 191.62
         * number : 4
         * proof : null
         * sm2key : null
         * materialMap : {}
         * materialNumberMap : {"1484804092155990019":3,"1484804092155990021":1}
         * recordStatus : 已关闭
         * materialTotalNumber : {}
         */

        private List<ElementBean> element;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<ElementBean> getElement() {
            return element;
        }

        public void setElement(List<ElementBean> element) {
            this.element = element;
        }

        public static class ElementBean {
            private int sign;
            private int status;
            private String createBy;
            private String updateBy;
            private String createDate;
            private String updateDate;
            private int executeApproval;
            private int applyApproval;
            private String recordId;
            private double value;
            private int number;
            private String proof;
            private String sm2key;
            /**
             * 1484804092155990019 : 3
             * 1484804092155990021 : 1
             */

            private String materialNumberMap;
            private String recordStatus;

            public int getSign() {
                return sign;
            }

            public void setSign(int sign) {
                this.sign = sign;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getUpdateDate() {
                return updateDate;
            }

            public void setUpdateDate(String updateDate) {
                this.updateDate = updateDate;
            }

            public int getExecuteApproval() {
                return executeApproval;
            }

            public void setExecuteApproval(int executeApproval) {
                this.executeApproval = executeApproval;
            }

            public int getApplyApproval() {
                return applyApproval;
            }

            public void setApplyApproval(int applyApproval) {
                this.applyApproval = applyApproval;
            }

            public String getRecordId() {
                return recordId;
            }

            public void setRecordId(String recordId) {
                this.recordId = recordId;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getProof() {
                return proof;
            }

            public void setProof(String proof) {
                this.proof = proof;
            }

            public String getSm2key() {
                return sm2key;
            }

            public void setSm2key(String sm2key) {
                this.sm2key = sm2key;
            }

            public String getRecordStatus() {
                return recordStatus;
            }

            public void setRecordStatus(String recordStatus) {
                this.recordStatus = recordStatus;
            }

            public String getMaterialNumberMap() {
                return materialNumberMap;
            }

            public void setMaterialNumberMap(String materialNumberMap) {
                this.materialNumberMap = materialNumberMap;
            }
        }
    }
}
