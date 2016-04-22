package yamato.vtracking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * Created by Gison on 9/4/16.
 */
@Generated("org.jsonschema2pojo")
public class Order implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("do_number")
    @Expose
    private String doNumber;
    @SerializedName("order_type")
    @Expose
    private String orderType;
    @SerializedName("cod_amount")
    @Expose
    private String codAmount;
    @SerializedName("exchange_code")
    @Expose
    private String exchangeCode;
    @SerializedName("payment_term")
    @Expose
    private String paymentTerm;
    @SerializedName("s_name")
    @Expose
    private String sName;
    @SerializedName("s_company_name")
    @Expose
    private String sCompanyName;
    @SerializedName("r_name")
    @Expose
    private String rName;
    @SerializedName("r_company_name")
    @Expose
    private String rCompanyName;
    @SerializedName("r_contact_number1")
    @Expose
    private String rContactNumber1;
    @SerializedName("r_contact_number2")
    @Expose
    private String rContactNumber2;
    @SerializedName("r_address1")
    @Expose
    private String rAddress1;
    @SerializedName("r_address2")
    @Expose
    private String rAddress2;
    @SerializedName("r_address3")
    @Expose
    private String rAddress3;
    @SerializedName("r_postcode")
    @Expose
    private String rPostcode;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("latest_status")
    @Expose
    private String latestStatus;
    @SerializedName("url")
    @Expose
    private String url;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The doNumber
     */
    public String getDoNumber() {
        return doNumber;
    }

    /**
     *
     * @param doNumber
     * The do_number
     */
    public void setDoNumber(String doNumber) {
        this.doNumber = doNumber;
    }

    /**
     *
     * @return
     * The orderType
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     *
     * @param orderType
     * The order_type
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     *
     * @return
     * The codAmount
     */
    public String getCodAmount() {
        return codAmount;
    }

    /**
     *
     * @param codAmount
     * The cod_amount
     */
    public void setCodAmount(String codAmount) {
        this.codAmount = codAmount;
    }

    /**
     *
     * @return
     * The exchangeCode
     */
    public String getExchangeCode() {
        return exchangeCode;
    }

    /**
     *
     * @param exchangeCode
     * The exchange_code
     */
    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    /**
     *
     * @return
     * The paymentTerm
     */
    public String getPaymentTerm() {
        return paymentTerm;
    }

    /**
     *
     * @param paymentTerm
     * The payment_term
     */
    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    /**
     *
     * @return
     * The sName
     */
    public String getSName() {
        return sName;
    }

    /**
     *
     * @param sName
     * The s_name
     */
    public void setSName(String sName) {
        this.sName = sName;
    }

    /**
     *
     * @return
     * The sCompanyName
     */
    public String getSCompanyName() {
        return sCompanyName;
    }

    /**
     *
     * @param sCompanyName
     * The s_company_name
     */
    public void setSCompanyName(String sCompanyName) {
        this.sCompanyName = sCompanyName;
    }

    /**
     *
     * @return
     * The rName
     */
    public String getRName() {
        return rName;
    }

    /**
     *
     * @param rName
     * The r_name
     */
    public void setRName(String rName) {
        this.rName = rName;
    }

    /**
     *
     * @return
     * The rCompanyName
     */
    public String getRCompanyName() {
        return rCompanyName;
    }

    /**
     *
     * @param rCompanyName
     * The r_company_name
     */
    public void setRCompanyName(String rCompanyName) {
        this.rCompanyName = rCompanyName;
    }

    /**
     *
     * @return
     * The rContactNumber1
     */
    public String getRContactNumber1() {
        return rContactNumber1;
    }

    /**
     *
     * @param rContactNumber1
     * The r_contact_number1
     */
    public void setRContactNumber1(String rContactNumber1) {
        this.rContactNumber1 = rContactNumber1;
    }

    /**
     *
     * @return
     * The rContactNumber2
     */
    public String getRContactNumber2() {
        return rContactNumber2;
    }

    /**
     *
     * @param rContactNumber2
     * The r_contact_number2
     */
    public void setRContactNumber2(String rContactNumber2) {
        this.rContactNumber2 = rContactNumber2;
    }

    /**
     *
     * @return
     * The rAddress1
     */
    public String getRAddress1() {
        return rAddress1;
    }

    /**
     *
     * @param rAddress1
     * The r_address1
     */
    public void setRAddress1(String rAddress1) {
        this.rAddress1 = rAddress1;
    }

    /**
     *
     * @return
     * The rAddress2
     */
    public String getRAddress2() {
        return rAddress2;
    }

    /**
     *
     * @param rAddress2
     * The r_address2
     */
    public void setRAddress2(String rAddress2) {
        this.rAddress2 = rAddress2;
    }

    /**
     *
     * @return
     * The rAddress3
     */
    public String getRAddress3() {
        return rAddress3;
    }

    /**
     *
     * @param rAddress3
     * The r_address3
     */
    public void setRAddress3(String rAddress3) {
        this.rAddress3 = rAddress3;
    }

    /**
     *
     * @return
     * The rPostcode
     */
    public String getRPostcode() {
        return rPostcode;
    }

    /**
     *
     * @param rPostcode
     * The r_postcode
     */
    public void setRPostcode(String rPostcode) {
        this.rPostcode = rPostcode;
    }

    /**
     *
     * @return
     * The remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     *
     * @param remarks
     * The remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     *
     * @return
     * The latestStatus
     */
    public String getLatestStatus() {
        return latestStatus;
    }

    /**
     *
     * @param latestStatus
     * The latest_status
     */
    public void setLatestStatus(String latestStatus) {
        this.latestStatus = latestStatus;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
