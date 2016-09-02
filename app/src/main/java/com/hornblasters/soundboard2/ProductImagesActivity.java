package com.hornblasters.soundboard2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hornblasters.xml.Parser;
import com.hornblasters.xml.Product;
import com.squareup.picasso.Picasso;


public class ProductImagesActivity extends HornBlastersActivity {
    private static final String TAG = "ProductImageActivity";
    private static final String URL_XML = "https://us-east1.hornblasters.com/api/product.php?id=";
    private ViewPager viewPager;
    private LinearLayout layout;
    private HorizontalScrollView scrollView;
    private int children = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_images);
        drawerToggle.setDrawerIndicatorEnabled(false);

        int productId = getIntent().getExtras().getInt(INTENT_PRODUCT);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Started with intent; ID: " + productId);
        }

        new DownloadXmlTaskNative().execute(URL_XML + productId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store_product_images, menu);
        return true;
    }

    private void renderData(Product data) {
        setTitle(data.title);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), data));
        layout = (LinearLayout) findViewById(R.id.thumbnails);
        scrollView = (HorizontalScrollView) layout.getParent();

        int width = (int) Math.round(layout.getHeight() * 1.5); // 1.78 should be constant
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(8, 0, 8, 4);

        children = data.images.size();
        for (int i = 0; i < children; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(lp);
            imageView.setBackgroundResource(R.drawable.border_thumbnail);
            imageView.setTag(i);
            imageView.setFocusable(true);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem((int) v.getTag());
                }
            });
            if (i == 0) {
                imageView.setSelected(true);
            }
            layout.addView(imageView);
            Picasso.with(getApplicationContext()).load(data.images.get(i)).tag(TAG).fit().centerInside().into(imageView);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < children; i++) {
                    ImageView imageView = (ImageView) layout.getChildAt(i);
                    if (i == position) {
                        scrollView.smoothScrollTo(imageView.getLeft(), 0);
                        imageView.setSelected(true);
                    } else {
                        imageView.setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onDestroy() {
         cancelTasks();
        super.onDestroy();
    }

    private void cancelTasks() {
        Picasso.with(getApplicationContext()).cancelTag(TAG);
    }

    private class DownloadXmlTaskNative extends AsyncTask<String, Void, Product> {
        private static final String TAG = "DownloadXmlTaskNative";

        @Override
        protected Product doInBackground(String... urls) {
            try {
                return (Product) new Parser().parse(downloadCachedUrl(urls[0]));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product result) {
            if (result == null) {
                if (BuildConfig.DEBUG) {
                    showErrorPage("Failed to load XML.");
                } else {
                    finish();
                }
            } else {
                if (!ProductImagesActivity.this.isFinishing()) {
                    renderData(result);
                }
            }
        }
    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {
        private final static String TAG = "ImagePagerAdapter";
        public final Product product;
        private int count = 0;

        public ImagePagerAdapter(FragmentManager fm, Product product) {
            super(fm);
            this.product = product;
            count = product.images.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(product.images.get(position));
        }

        @Override
        public int getCount() {
            return count;
        }
    }
}
