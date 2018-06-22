package info.guardianproject.pinwheel.model;

import java.util.Date;

public class Notification {

    public String chimeId;
    public Date happened;
    public String description;
    public int type;

    public final static int TYPE_ADDED_CHIME = 0;
    public final static int TYPE_HEARD_KNOWN_CHIME = 1;
    public final static int TYPE_FOUND_NEW_CHIME = 2;

}
