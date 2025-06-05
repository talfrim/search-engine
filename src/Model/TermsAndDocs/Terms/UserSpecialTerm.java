package Model.TermsAndDocs.Terms;

public class UserSpecialTerm extends Term {
    public UserSpecialTerm(String data) {
        super(data);
    }

    @Override
    public String getType() {
        return "UserSpecialTerm";
    }
}
