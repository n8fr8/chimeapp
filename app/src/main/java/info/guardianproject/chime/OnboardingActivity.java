package info.guardianproject.chime;

import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    public void init ()
    {
        setGradientBackground();

        int iconWidth = 480;
        int iconHeight = 480;
        int marginTop = 12;
        int marginLeft = 12;
        int marginRight = 12;
        int marginBottom = 12;

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        AhoyOnboarderCard ahoyOnboarderCard =
                new AhoyOnboarderCard(getString(R.string.app_name), "Can you hear it? You are ready to join the Wind network!", R.mipmap.ic_launcher);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_nearby), "Chime helps you discover and connect with nearby, off-grid services in your area", R.drawable.ic_nearby_white);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_dashboard), "Save and share your discoveries with neighbors and strangers along the way", R.drawable.ic_dashboard_white_24dp);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_notifications), "Chime will let you know, when you've got a connection!", R.drawable.ic_notifications_white_24dp);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        finish();
    }
}
