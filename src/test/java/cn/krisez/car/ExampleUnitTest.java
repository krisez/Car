package cn.krisez.car;

import com.google.gson.JsonObject;

import org.junit.Test;

import cn.krisez.car.Network.MySubscribe;
import cn.krisez.car.Network.NetUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void add(){
        NetUtil.INSTANCE().create(new MySubscribe<JsonObject>() {

            @Override
            public void onNext(JsonObject jsonObject) {
                System.out.println(jsonObject.toString());
            }

            @Override
            public void onComplete() {

            }
        },"123","123",true);
    }
}