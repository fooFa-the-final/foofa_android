package com.example.foofatest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.foofatest.Adapter.FollowListAdapter;
import com.example.foofatest.dto.Follow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MemberFollowActivity extends AppCompatActivity {

    private List<Follow> follows;
    private FollowListAdapter adapter;
    private SharedPreferences prefs;
    private Button ufbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_follow);
        ListView list = (ListView) findViewById(R.id.list);

        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        final String fromId = prefs.getString("id", "");
        follows = new ArrayList<>();
        adapter = new FollowListAdapter(this, follows);
        ufbtn = (Button) findViewById(R.id.ufbtn);

        ufbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
                final String fromId = prefs.getString("id", "");
                new FollowLoadingTask().execute("http://foofa.crabdance.com:8888/FoodtruckFinderProject/mobile/follow/list.do?Fromid=" + fromId);

            }

        });

    }

    public class FollowLoadingTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

                try {
                    URL url = new URL((String) params[0]);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(url.openStream()));
                    NodeList nodeList = doc.getElementsByTagName("follows");
                    for (int i = 0; i < nodeList.getLength(); i++) {

                        Node node = nodeList.item(i);
                        Element element = (Element) node;
                        Follow follow = new Follow();
                        follow.setFromId(getTagValue("FromId", element));
                        follow.setToId(getTagValue("toId", element));
                        follow.setFollowCount(getTagValue("followCount", element));
                        follows.add(follow);

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;

            }
        }

        private static String getTagValue(String tag, Element element) {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node value = (Node) nodeList.item(0);
            return value.getNodeValue();
        }
    }
