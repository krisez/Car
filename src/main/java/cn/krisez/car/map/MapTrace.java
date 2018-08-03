package cn.krisez.car.map;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.krisez.car.Network.MySubscribe;
import cn.krisez.car.Network.NetUtil;
import cn.krisez.car.R;
import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.ui.IMainView;

public class MapTrace {
    private Polyline mPolyline;
    private List<CarRoute> mList;

    private AMap mAMap;
    private IMainView mIMainView;

    MapTrace(MapView mapView, IMainView view) {
        this.mAMap = mapView.getMap();
        mList = new ArrayList<>();
        this.mIMainView = view;
    }

    public void startTrace(String id) {
        NetUtil.INSTANCE().create(new MySubscribe<List<CarRoute>>() {
            @Override
            public void onNext(List<CarRoute> carRoutes) {
                mList.addAll(carRoutes);
            }

            @Override
            public void onComplete() {
                addPolylineInPlayGround(mList);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mIMainView.error(e.getMessage());
            }
        }, id, true);
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    /**
     * 添加轨迹线
     *
     * @param points
     */
    private void addPolylineInPlayGround(List<CarRoute> points) {
        List<LatLng> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(new LatLng(points.get(i).getLat(), points.get(i).getLon()));
        }
        List<Integer> colorList = new ArrayList<Integer>();
        List<BitmapDescriptor> bitmapDescriptors = new ArrayList<BitmapDescriptor>();

        int[] colors = new int[]{Color.argb(255, 0, 255, 0), Color.argb(255, 255, 255, 0), Color.argb(255, 255, 0, 0)};

        //用一个数组来存放纹理
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));

        List<Integer> texIndexList = new ArrayList<Integer>();
        texIndexList.add(0);//对应上面的第0个纹理
        texIndexList.add(1);
        texIndexList.add(2);

        Random random = new Random();
        for (int i = 0; i < list.size(); i++) {
            colorList.add(colors[random.nextInt(3)]);
            bitmapDescriptors.add(textureList.get(0));

        }

        mPolyline = mAMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture)) //setCustomTextureList(bitmapDescriptors)
//				.setCustomTextureIndex(texIndexList)
                .addAll(list)
                .useGradient(true)
                .width(18));

        // LatLng latLng = new LatLng((list.get(0).latitude+list.get(list.size()-1).latitude)/2,
        //    (list.get(0).longitude+list.get(list.size()-1).longitude)/2);

        //LatLngBounds bounds = new LatLngBounds(queryPoints(list,1), queryPoints(list,2));
        LatLngBounds.Builder builder = LatLngBounds.builder();

        for (int i = 0; i < list.size() - 1; i++) {
            builder.include(list.get(i));
        }
//
//        Log.d("MapTrace", "addPolylineInPlayGround:" + bounds.northeast);
//        Log.d("MapTrace", "addPolylineInPlayGround:" + bounds.southwest);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
        mIMainView.traceOver();
    }

    /**
     * 动画效果
     */
    public Animator setAnimation(final Marker marker, float duration) {
        ValueAnimator animator;
        Object[] objects = new Object[mList.size()];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = mList.get(i);
        }
        animator = ValueAnimator.ofObject((TypeEvaluator<CarRoute>) (fraction, startValue, endValue) -> {
            double lat = startValue.getLat() + fraction * (endValue.getLat() - startValue.getLat());
            double lon = startValue.getLon() + fraction * (endValue.getLon() - startValue.getLon());
            double speed = startValue.getSpeed() + fraction * (endValue.getSpeed() - startValue.getSpeed());
            return new CarRoute(lon, lat, speed, getAngle(startValue.getLatLng(), endValue.getLatLng()));
        }, objects);

        animator.addUpdateListener(animation -> {
            CarRoute carRoute = (CarRoute) animation.getAnimatedValue();
            marker.setMarkerOptions(marker.getOptions().position(carRoute.getLatLng()).rotateAngle((float) carRoute.getBearing()));
            //默认无
            //  mAMap.moveCamera(CameraUpdateFactory.changeLatLng(carRoute.getLatLng()));
        });
        animator.setInterpolator(new SpeedInterpolator(mList, duration));
        animator.setDuration((long) duration);
        animator.start();
        return animator;
    }

    private float getAngle(LatLng latLng1, LatLng latLng2) {
        if (latLng1 == null || latLng2 == null)
            return 0;
        float angle = 0;
        if (latLng2.longitude - latLng1.longitude == 0) {
            angle = latLng1.latitude > latLng2.latitude ? 180 : 0;
        } else if (latLng2.latitude - latLng1.latitude == 0) {
            angle = latLng1.longitude < latLng2.longitude ? 90 : 270;
        } else {
            double a = (latLng2.latitude - latLng1.latitude) / (latLng2.longitude - latLng1.longitude);
            if (latLng2.longitude > latLng1.longitude) {
                angle = (float) (Math.atan(a) * 180 / Math.PI) - 90;
            } else {
                angle = (float) (Math.atan(a) * 180 / Math.PI) + 90;
            }
        }
        return angle;
    }

    class SpeedInterpolator implements TimeInterpolator {
        private List<CarRoute> mSpeedList;
        private double totalDistance = 0;
        private float t;

        int index;
        private float temp[];

        private double[] a;
        private double pretentDistance;
        private double[] s;
        private double mMultiple;

        SpeedInterpolator(List<CarRoute> speedList, float duration) {
            mSpeedList = speedList;
            t = duration / 1000 / (speedList.size() - 1);
            a = new double[speedList.size() - 1];
            mMultiple = duration / 1000 ;
            //真实距离
            for (int i = 0; i < speedList.size() - 1; ++i) {
                double k = (double) AMapUtils.calculateLineDistance(speedList.get(i).getLatLng(), speedList.get(i + 1).getLatLng());
                this.totalDistance += k;
            }
            //加速度
            for (int i = 0; i < speedList.size() - 1; i++) {
                a[i] = (speedList.get(i + 1).getSpeed() - speedList.get(i).getSpeed()) / t;
            }

            //中间段的时间
            float tp = 1f / (speedList.size() - 1);
            temp = new float[speedList.size()-1];
            temp[0] = 0;
            for (int i = 1; i < temp.length; i++) {
                temp[i] = temp[i-1] + tp;
            }

            //模拟距离
            s = new double[mSpeedList.size() - 1];
            for (int i = 0; i < mSpeedList.size() - 1; i++) {
                float start = (float) (mSpeedList.get(i).getSpeed() / 3.6f);
                s[i] = start * t + a[i] * t * t / 2;
                pretentDistance += s[i];
            }
        }

        /**
         * 完成度时间
         *
         * @param input 可看之为时间进行了多久
         *              单位km/h 换算 m/s
         * @return 可反映速度快慢的返回值
         */

        @Override
        public float getInterpolation(float input) {
            index = (int) (input * (mSpeedList.size() - 1));
            if (index == mSpeedList.size() - 1) {
                return 1;
            }
            double ss = 0;
            for (int i = 0; i < index; i++) {
                ss += s[i];
            }
            float start = (float) (mSpeedList.get(index).getSpeed() / 3600 * 1000);
            //double time = input % t;//当前经历的时间
            //s += v * time + a * time * time / 2;

            //t  duration/size-1    c 1/size-1
            double c = input - temp[index];
            c *= mMultiple;
            double current = start * c + a[index] * c * c / 2;
            float v = (float) (start + a[index] * c);//当前速度
            ss += current;
            //EventBus.getDefault().post(new SpeedEvent((v * 3.6f)));
            String speed = String.format(Locale.CHINA, "%.2f", (v * 3.6f));
            mIMainView.speed(speed);
            //实际距离，真实距离    totalDistance
            return (float) (ss / pretentDistance);
        }
    }
}
