package android.myapplication.View;

import android.myapplication.Controller.ViewPagerAdapter;
import android.myapplication.R;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ImageView ivBack = findViewById(R.id.ivBack);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Đặt món");
                        break;
                    case 1:
                        tab.setText("Đặt chỗ");
                        break;
                }
            }
        }).attach();


        // Đặt background cho tab khi được chọn và không được chọn
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.view.setBackgroundResource(R.drawable.tab_background_selected);
                if (tab.getPosition() == 0) {
                    tab.view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_right_corner_background));
                } else if (tab.getPosition() == 1) {
                    tab.view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_left_corner_background));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.view.setBackgroundResource(R.drawable.tab_background_unselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Không cần làm gì khi tab được chọn lại
            }
        });

        // Thiết lập background mặc định cho tab "Đặt món" khi khởi tạo
        TabLayout.Tab defaultTab = tabLayout.getTabAt(0);
        if (defaultTab != null) {
            defaultTab.view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_right_corner_background));
        }
    }
}
