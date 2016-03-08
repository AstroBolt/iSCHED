package mobcom.iacademy.thesis.routine.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import mobcom.iacademy.thesis.routine.days.FridayFragment;
import mobcom.iacademy.thesis.routine.days.MondayFragment;
import mobcom.iacademy.thesis.routine.days.SaturdayFragment;
import mobcom.iacademy.thesis.routine.days.SundayFragment;
import mobcom.iacademy.thesis.routine.days.ThursdayFragment;
import mobcom.iacademy.thesis.routine.days.TuesdayFragment;
import mobcom.iacademy.thesis.routine.days.WednesdayFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SundayFragment tab1 = new SundayFragment();
                return tab1;
            case 1:
                MondayFragment tab2 = new MondayFragment();
                return tab2;
            case 2:
                TuesdayFragment tab3 = new TuesdayFragment();
                return tab3;
            case 3:
                WednesdayFragment tab4 = new WednesdayFragment();
                return tab4;
            case 4:
                ThursdayFragment tab5 = new ThursdayFragment();
                return tab5;
            case 5:
                FridayFragment tab6 = new FridayFragment();
                return tab6;
            case 6:
                SaturdayFragment tab7 = new SaturdayFragment();
                return tab7;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
