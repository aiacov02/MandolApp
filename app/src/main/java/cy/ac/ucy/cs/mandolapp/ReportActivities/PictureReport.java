package cy.ac.ucy.cs.mandolapp.ReportActivities;


/**
 * Created by andreas on 09/04/2018.
 */

public class PictureReport extends Report {

    private byte[] image;
    private String location;

    public PictureReport(String app, byte[] image, String location, String Description, String[] Categories, String[] Authorities,String Date){
        super(app,null,Description,Categories,Authorities,Date);
        this.image = image;
        this.location = location;
    }

    public PictureReport(String app, byte[] image, String location,String Description, String[] Categories, String[] Authorities){
        super(app,null,Description,Categories,Authorities);
        this.image = image;
        this.location = location;
    }

    public PictureReport(String id, String app,byte[] image, String location, String Description, String[] Categories, String[] Authorities,String Date){
        super(id, app,null,Description,Categories,Authorities,Date);
        this.image = image;
        this.location = location;
    }

    public byte[] getImage(){
        return this.image;
    }

    public String getLocation(){
        return this.location;
    }
}
