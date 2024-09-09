package android.myapplication.Controller;

import android.myapplication.View.OrderHistoryFragment;
import android.myapplication.View.ReservationHistoryFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OrderHistoryFragment();
            case 1:
                return new ReservationHistoryFragment();
            default:
                return new OrderHistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
