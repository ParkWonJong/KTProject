package com.example.user.ktproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapInfo;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TMapView tmapview = null;
    private FrameLayout map_layout = null;
    public static Context mContext = null;
    private JSONObject map = null;
    private TMapData tmapdata = null;

    public TMapPolyLine makePolyLine(List<TMapPoint> list) {
        TMapPolyLine line = new TMapPolyLine();
        Iterator<TMapPoint> it = list.iterator();

        while (it.hasNext()) {
            line.addLinePoint((TMapPoint) it.next());

        }

        return line;
    }

    public List<TMapPoint> getPoint(JSONObject map) {
        JSONArray mapArray = (JSONArray) map.get("features");
        JSONObject prvit = null;
        JSONArray cooperatesArr = null;
        JSONArray trueCooperateArr = null;

        String roadType = null;

        double x = 0;
        double y = 0;
        int i = 0;
        List<TMapPoint> list = Collections
                .synchronizedList(new ArrayList<TMapPoint>());

        Log.i("papa", "mama");
        Log.i("uuu", Integer.valueOf(mapArray.size()).toString());
        for (int temp = 0; temp < mapArray.size(); temp++) {
            prvit = (JSONObject) ((JSONObject) (mapArray.get(temp)))
                    .get("geometry");
            roadType = (String) prvit.get("type");
            Log.i("rodtype", roadType);
            if (roadType.contains("LineString")) {
                cooperatesArr = (JSONArray) prvit.get("coordinates");

                for (int j = 0; j < cooperatesArr.size(); j++) {
                    trueCooperateArr = (JSONArray) cooperatesArr.get(j);
                    Iterator<Double> iterator = trueCooperateArr.iterator();

                    while (iterator.hasNext()) {

                        if (i == 0) {
                            x = ((iterator.next().doubleValue()));
                            Log.i("xxx", Double.valueOf(x).toString());
                            i++;
                        } else {
                            y = ((iterator.next().doubleValue()));
                            i = 0;
                            break;

                        }
                        i++;
                    }
                    TMapPoint point = new TMapPoint(y, x);
                    list.add(point);
                }
            }

        }

        return list;

    }

    public void printPath(JSONObject map) {

        List<TMapPoint> list = getPoint(map);

        tmapview.addTMapPath(makePolyLine(list));
        // zoomToMapPoint(list.get(0), list.get(5));
        properZoomToMapPoint(list);

    }

    public boolean properZoomToMapPoint(List<TMapPoint> list) {
        int zoomLevel;
        TMapPoint tmpPoint;

        ArrayList<TMapPoint> tmpList = new ArrayList<TMapPoint>();
        tmpList.addAll(list);
        TMapInfo info = tmapview.getDisplayTMapInfo(tmpList);

        zoomLevel = info.getTMapZoomLevel();
        tmpPoint = info.getTMapPoint();
        tmapview.setZoomLevel(zoomLevel - 1);
        tmapview.setCenterPoint(tmpPoint.getLongitude(), tmpPoint.getLatitude());
        // tmapview.setZoomLevel(13);
        Log.i("줌 레벨", "이거 되는 거임?");

        return true;
    }

    class TMapPath extends AsyncTask<Double, Void, Boolean> {

        protected Boolean doInBackground(Double... values) {

            URL url = null;
            try {
                //values[0]~[3]
                url = new URL(
                        "https://apis.skplanetx.com/tmap/routes?version=1&startX="
                                + values[0]
                                + "&startY="
                                + values[1]
                                + "&endX="
                                + values[2]
                                + "&endY="
                                + values[3]
                                + "&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&appKey=10ce0aca-203f-3df8-88c5-28f83d8cb8e7");
                Log.i("value[0]", Double.valueOf(values[0]).toString());
                Log.i("value[1]", Double.valueOf(values[1]).toString());

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(url.openConnection()
                        .getInputStream(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                map = (JSONObject) JSONValue.parseWithException(isr);
                Log.i("mapData", map.toJSONString()); // 내가 추가.
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.i("map1234", "null");

            if (map == null) {
                Log.i("map1000", "null");
                return Boolean.FALSE;
            }

            printPath(map);

            return Boolean.TRUE;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        tmapview = new TMapView(mContext);
        tmapdata = new TMapData();

        map_layout = (FrameLayout) findViewById(R.id.map_layout_show);
        addLayout(map_layout);

        //자동차 예제
        Double startX = 126.98217734415019;
        Double startY = 37.56468648536046;
        Double endX = 129.07579349764512;
        Double endY = 35.17883196265564;

        //보행자 예제
        /*Double startX = 126.92365493654832;
        Double startY = 37.556770374096615;
        Double endX = 126.92432158129688;
        Double endY = 37.55279861528311;*/


        TMapPath task = new TMapPath();
        task.execute(startX, startY, endX, endY);


    }

    public boolean setMap() {

        if (tmapview == null) {
            Log.i("viewNo", "noView");
            return false;
        }

        tmapview.setSKPMapApiKey("10ce0aca-203f-3df8-88c5-28f83d8cb8e7"); // dd973b8b-da37-37f1-8b0d-dcc516714f07
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapview.setZoomLevel(12);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);

        Log.i("셋맵", "트루");
        return true;
    }

    public boolean addLayout(FrameLayout mapLayout) {
        if (mapLayout == null) {
            Log.i("프레임문제", "뭐가");
            return false;
        }

        setMap();
        mapLayout.addView(tmapview);
        Log.i("애드레이", "트루");
        return true;
    }
}
