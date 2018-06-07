package cy.ac.ucy.cs.mandolapp.ReportActivities;

import java.io.Serializable;

/**
 * Created by andreas on 10/11/2017.
 */

public class Report implements Serializable {


    private String id;
    private String App;
    private String Url;
    private String Description;
    private String[] Categories;
    private String[] Authorities;
    private String Date;
    private byte[] image;
    private String location;


    public Report(String app, String Url, String Description, String[] Categories, String[] Authorities){
        this.App = app;
        this.Url = Url;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = null;
    }

    public Report(String id,String app, String Url, String Description, String[] Categories, String[] Authorities,String Date){
        this.id=id;
        this.App = app;
        this.Url = Url;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = Date;

    }

    public Report(String app, String Url, String Description, String[] Categories, String[] Authorities,String Date){
        this.App = app;
        this.Url = Url;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = Date;

    }

    public Report(String app, byte[] image, String location, String Description, String[] Categories, String[] Authorities){
        this.App = app;
        this.image = image;
        this.location = location;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = null;
    }

    public Report(String id,String app,byte[] image, String location, String Description, String[] Categories, String[] Authorities,String Date){
        this.id=id;
        this.App = app;
        this.image = image;
        this.location = location;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = Date;

    }

    public Report(String app, byte[] image, String location, String Description, String[] Categories, String[] Authorities,String Date){
        this.App = app;
        this.image = image;
        this.location = location;
        this.Description = Description;
        this.Categories = Categories;
        this.Authorities = Authorities;
        this.Date = Date;

    }


    public byte[] getImage(){
        return this.image;
    }

    public String getLocation(){
        return this.location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp() {
        return App;
    }

    public void setApp(String app) {
        App = app;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String[] getCategories() {
        return Categories;
    }

    public void setCategories(String[] categories) {
        this.Categories = categories;
    }

    public void setAuthorities(String[] authorities){this.Authorities = authorities;}

    public String[] getAuthorities(){return this.Authorities;}

    public String getDate(){return this.Date;}

    public void setDate(String Date){this.Date=Date;}

}
