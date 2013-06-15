package cheng.app.nga.task;

import android.text.TextUtils;

import cheng.app.nga.activity.ReplyActivity;
import cheng.app.nga.content.TopicKeysEntry;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;

public class LoadKeyTask extends ProgressTask<Integer, ArrayList<TopicKeysEntry>, ReplyActivity> {

    public LoadKeyTask(ReplyActivity target, int text) {
        super(target, text);
    }

    @Override
    protected ArrayList<TopicKeysEntry> doInBackground(ReplyActivity target, Integer... params) {
        int fid = params[0];
        String result =
                HttpUtil.httpGet(Configs.NUKE_URL + Configs.TOPIC_KEY + fid + "&time=" +new Date().getHours(), null, null, 5000);
        if (!TextUtils.isEmpty(result) && result.indexOf("{\"data\"") != -1) {
            String text = result.substring(result.indexOf("{\"data\""));
            return JsonUtil.parseTopicKeys(text);
        }
        return null;
    }

    @Override
    protected void onPostExecute(ReplyActivity target, ArrayList<TopicKeysEntry> result) {
        if (target != null && !target.isFinishing()) {
            target.onKeysLoaded(result);
        }
        super.onPostExecute(target, result);
    }
}

