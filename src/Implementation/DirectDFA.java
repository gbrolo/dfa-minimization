package Implementation;

/**
 * Created by Gabriel Brolo on 05/08/2017.
 */
public class DirectDFA {
    private String postFixRegexp;
    private String extendedPostFixRegexp;
    private String[] charList;

    public DirectDFA (String postFixRegexp) {
        this.postFixRegexp = postFixRegexp;
        setExtendedPostFixRegexp();
    }

    private void setExtendedPostFixRegexp () {
        this.extendedPostFixRegexp = this.postFixRegexp + "#.";
    }

    private void setCharList () {
        this.charList = new String[this.extendedPostFixRegexp.length()];
        for (int i = 0; i < extendedPostFixRegexp.length(); i++) {
            this.charList[i] = Character.toString(extendedPostFixRegexp.charAt(i));
        }
    }


}
