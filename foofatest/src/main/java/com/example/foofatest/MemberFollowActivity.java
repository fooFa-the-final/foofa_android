package com.example.foofatest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.foofatest.Adapter.FollowListAdapter;
import com.example.foofatest.dto.Follow;
import com.example.foofatest.dto.Member;

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

    private List<Member> members = new ArrayList<>();
    private ListView listView;
    private List<Follow> follows = new ArrayList<>();
    private FollowListAdapter adapter;
    private SharedPreferences prefs;
    private String fromId;
    private String toId;
    private Button ufbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_follow);
        prefs = getSharedPreferences("loginUserId", Context.MODE_PRIVATE);
        fromId = prefs.getString("loginId", "");
        if (fromId.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        members = new ArrayList<>();
        new FollowLoadingTask().execute("http://10.0.2.2:8888/FoodtruckFinderProject/mobile/follow/list.do?fromId=" + fromId);
        listView = (ListView) findViewById(R.id.followList);
        adapter = new FollowListAdapter(this, members);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//            맴버아이디 나오나 확인
//                Toast.makeText(MemberFollowActivity.this, members.get(position).getMemberId(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),FollowProfileActivity.class);
                intent.putExtra("memberId", members.get(position).getMemberId());
                intent.putExtra("birthday", members.get(position).getBirthday());
                intent.putExtra("email", members.get(position).getEmail());
                intent.putExtra("profileImg", members.get(position).getProfileImg());
                startActivity(intent);
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
                    NodeList nodeList = doc.getElementsByTagName("member");
                    for (int i = 0; i < nodeList.getLength(); i++) {

                        Node node = nodeList.item(i);
                        Element element = (Element) node;
                        Member member = new Member();
                        member.setMemberId(getTagValue("memberId", element));
                        member.setBirthday(getTagValue("birthday", element));
                        member.setEmail(getTagValue("email", element));
                        member.setGender(fromId);
//                        member.setFollowCount(Integer.parseInt(getTagValue("followCount", element)));
//                        member.setReviewCount(Integer.parseInt(getTagValue("reviewCount",element)));
                        member.setProfileImg("http://10.0.2.2:8888/FoodtruckFinderProject/resources/img/member/"+getTagValue("profileImg",element));
                        members.add(member);

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

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }


    private static String getTagValue(String tag, Element element){

        if(element.getElementsByTagName(tag).item(0)==null){
            return "";
        }

        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node)nodeList.item(0);
        return  node.getNodeValue();
    }
    private class FollowTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL((String) params[0]);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(url.openStream()));

                NodeList nodeList = doc.getElementsByTagName("Follow");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Follow follow = new Follow();
                    Node node = nodeList.item(i);
                    Element element = (Element) node;
                    follow.setFromId(getTagValue("FromId", element));
                    follow.setToId(getTagValue("toId", element));

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
}
