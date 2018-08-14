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
                new AhoyOnboarderCard(getString(R.string.app_name), getString(R.string.onboarding_welcome), R.mipmap.ic_launcher);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_nearby), getString(R.string.onboarding_chime), R.drawable.ic_nearby_white);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_dashboard), getString(R.string.onboarding_share), R.drawable.ic_dashboard_white_24dp);
        ahoyOnboarderCard.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard.setTitleColor(R.color.white);
        ahoyOnboarderCard.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard.setDescriptionTextSize(dpToPixels(8, this));
        ahoyOnboarderCard.setIconLayoutParams(iconWidth, iconHeight, marginTop, marginLeft, marginRight, marginBottom);

        pages.add(ahoyOnboarderCard);

        ahoyOnboarderCard = new AhoyOnboarderCard(getString(R.string.title_notifications), getString(R.string.onboarding_notify), R.drawable.ic_notifications_white_24dp);
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
