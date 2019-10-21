package domain;

import java.time.Instant;

public class FormulaireConsent {

    private Contact contact;
    private boolean hasAcceptedConsent;
    private Representant representant = null;
    private long createTime= 0;

    public FormulaireConsent() {
        this.createTime = Instant.now().getEpochSecond();
    }

    public FormulaireConsent(Contact contact, boolean hasAcceptedConsent) {
        this.createTime = Instant.now().getEpochSecond();
        this.hasAcceptedConsent = hasAcceptedConsent;
        this.createTime = Instant.now().getEpochSecond();
    }

    public FormulaireConsent(Contact contact, boolean hasAcceptedConsent, Representant representant) {
        this.contact = contact;
        this.hasAcceptedConsent = hasAcceptedConsent;
        this.representant = representant;
        this.createTime = Instant.now().getEpochSecond();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isHasAcceptedConsent() {
        return hasAcceptedConsent;
    }

    public void setHasAcceptedConsent(boolean hasAcceptedConsent) {
        this.hasAcceptedConsent = hasAcceptedConsent;
    }

    public Representant getRepresentant() {
        return representant;
    }

    public void setRepresentant(Representant representant) {
        this.representant = representant;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
