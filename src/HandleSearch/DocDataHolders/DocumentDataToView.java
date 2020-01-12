package HandleSearch.DocDataHolders;

public class DocumentDataToView {
    String docNo;
    String date;
    String entities;

    public DocumentDataToView(String docNo){
        this.docNo = docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEntities(String entities) {
        this.entities = entities;
    }

    public String getDocNo() {
        return docNo;
    }

    public String getDate() {
        return date;
    }

    public String getEntities() {
        return entities;
    }
}
