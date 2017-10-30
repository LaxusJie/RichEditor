package com.app.richeditor.editor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;

import com.app.base.network.entity.EditorDataEntity;
import com.app.richeditor.R;
import com.app.richeditor.utils.RegexUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

/**
 * desc：编辑器内容管理类
 * author：mgq
 * date：2017-06-06
 */
public class RichTextEditorManager {
    /*编辑器所展示数据的集合*/
    private List<EditorDataEntity> editShowList = new ArrayList<>();
    /*编辑器中多媒体数据集合*/
    private List<EditorDataEntity> editorDataList = new ArrayList<>();
    /*富文本编辑器*/
    private RichTextEditor editor;
    /*上下文*/
    private Context mContext;
    private boolean richEditorStatus = true;
    private int editorBackground;
    private String[] attrArray = {EditorConstants.ATTR_NAME, EditorConstants.ATTR_ALIASNAME, EditorConstants.ATTR_PATH, EditorConstants.ATTR_POST};
    private String[] typeArray = {RichTextEditor.TYPE_ALINK, RichTextEditor.TYPE_IMAGE, RichTextEditor.TYPE_AUDIO, RichTextEditor.TYPE_VIDEO};
    private String[] regArray = {EditorConstants.REGEX_ATTACHMENT, EditorConstants.REGEX_IMG, EditorConstants.REGEX_AUDIO, EditorConstants.REGEX_VEDIO};

    public RichTextEditorManager(Context mContext) {
        this.mContext = mContext;
        editorBackground = 0;
    }

    public RichTextEditorManager(RichTextEditor editor, Context mContext) {
        this.editor = editor;
        this.mContext = mContext;
        editorBackground = 0;
    }

    private boolean isRichEditorStatus() {
        return richEditorStatus;
    }

    public void setRichEditorStatus(boolean richEditorStatus) {
        this.richEditorStatus = richEditorStatus;
    }

    /*回显数据*/
    private void showData(boolean status) {
        int k = 0;
        for (int i = 0; i < editShowList.size(); i++) {
            if (editShowList.get(i).getType().equals(RichTextEditor.TYPE_TEXT) || editShowList.get(i).getType().equals(RichTextEditor.TYPE_ALINK)) {
                if (editShowList.get(i).getText().trim().length() <= 0) {
                    continue;
                }
                editor.addEditTextAtIndex(k, editShowList.get(i).getText(), editShowList.get(i).getType());
            } else if (editShowList.get(i).getType().equals(RichTextEditor.TYPE_AUDIO)) {
                editor.addAudioViewAtIndex(k, editShowList.get(i));
            } else if (editShowList.get(i).getType().equals(RichTextEditor.TYPE_VIDEO)) {
                editor.addImageViewAtIndex(k, editShowList.get(i));
            } else {
                editor.addImageViewAtIndex(k, editShowList.get(i));
            }
            k++;
            editor.showEmojiIcon();
        }
        editor.setRichTextEditorStatus(status);
    }

    /*过滤html数据*/
    public void filterHtml(final String htmlStr) {
        //过滤
        filterData(htmlStr);
        //排序
        sortMediaPosition();
    }

    /**
     * 过滤数据
     *
     * @param htmlStr 源文本
     * @return 过滤后的文本
     */
    public String filterData(String htmlStr) {
        // 替换换行
        htmlStr = htmlStr.replace("<br>", "\n");
        htmlStr = htmlStr.replace("</p>", "\n");
        // 过滤script标签
        htmlStr = RegexUtils.getFilterString(EditorConstants.REGEX_SCRIPT, htmlStr, "");
        // 过滤style标签
        htmlStr = RegexUtils.getFilterString(EditorConstants.REGEX_STYLE, htmlStr, "");
        // 过滤html标签（除img、video、audio、a标签）
        htmlStr = RegexUtils.getFilterString(EditorConstants.REGEX_HTML, htmlStr, "");
        //过滤多媒体标签之前拆分数据
        splitTextData(htmlStr);

        //过滤附件a标签
        htmlStr = getMediaFilterString(EditorConstants.REGEX_ATTACHMENT, EditorConstants.ATTR_HREF, htmlStr, RichTextEditor.TYPE_ALINK);
        //过滤图片
        htmlStr = getMediaFilterString(EditorConstants.REGEX_IMG, EditorConstants.ATTR_SRC, htmlStr, RichTextEditor.TYPE_IMAGE);
        //过滤音频
        htmlStr = getMediaFilterString(EditorConstants.REGEX_AUDIO, EditorConstants.ATTR_SRC, htmlStr, RichTextEditor.TYPE_AUDIO);
        //过滤视频
        htmlStr = getMediaFilterString(EditorConstants.REGEX_VEDIO, EditorConstants.ATTR_SRC, htmlStr, RichTextEditor.TYPE_VIDEO);

        /*确定多媒体标签的位置*/
        for (String aTypeArray1 : typeArray) {
            ensureMediaTagPosition(htmlStr, aTypeArray1);
        }

        for (String aTypeArray : typeArray) {
            htmlStr = replaceTagByUrl(htmlStr, aTypeArray);
        }
        return htmlStr;
    }

    /*将tag标签替换为对应的url*/
    private String replaceTagByUrl(String htmlStr, String tag) {
        for (int i = 0; i < editorDataList.size(); i++) {
            if (tag.equals(editorDataList.get(i).getType())) {
                if (tag.equals(RichTextEditor.TYPE_ALINK)) {
                    htmlStr = htmlStr.replaceFirst(tag, editorDataList.get(i).getText());
                } else {
                    htmlStr = htmlStr.replaceFirst(tag, editorDataList.get(i).getUrl());
                }
            }
        }
        return htmlStr;
    }

    /**
     * 确定多媒体标签的位置
     *
     * @param htmlStr 源文本
     */
    private void ensureMediaTagPosition(String htmlStr, String tag) {
        int position = -8;
        for (int i = 0; i < editorDataList.size(); i++) {
            position = htmlStr.indexOf(tag, position + 8);
            for (int k = 0; k < editorDataList.size(); k++) {
                if (editorDataList.get(k).getPosition() == 0 && tag.equals(editorDataList.get(k).getType())) {
                    editorDataList.get(k).setPosition(position);
                    break;
                }
            }
        }
    }

    /**
     * 获取过滤多媒体标签后的数据
     */
    private String getMediaFilterString(String element, String attr, String htmlStr, String tag) {
        EditorDataEntity editorDataEntity;
        String reg = RegexUtils.getRegExpression(element, attr);
        Matcher matcher = RegexUtils.getMatcher(reg, htmlStr);
        while (matcher.find()) {
            editorDataEntity = new EditorDataEntity();
            String mediaLabel = matcher.group();
            editorDataEntity.setType(tag);
            if (tag.equals(RichTextEditor.TYPE_ALINK)) {
                editorDataEntity.setText(matcher.group(1));
            } else {
                editorDataEntity.setUrl(matcher.group(1));
            }
            getLabelAttr(element, mediaLabel, editorDataEntity);
            editorDataList.add(editorDataEntity);
        }
        htmlStr = matcher.replaceAll(tag);
        return htmlStr;
    }

    /*获取html标签属性*/
    private void getLabelAttr(String element, String content, EditorDataEntity editorDataEntity) {
        for (int i = 0; i < attrArray.length; i++) {
            String reg = RegexUtils.getRegExpression(element, attrArray[i]);
            Matcher matcher = RegexUtils.getMatcher(reg, content);
            if (matcher.find()) {
                switch (i) {
                    case 0:
                        editorDataEntity.setName(matcher.group(1));
                        break;
                    case 1:
                        editorDataEntity.setAliasname(matcher.group(1));
                        break;
                    case 2:
                        editorDataEntity.setPath(matcher.group(1));
                        break;
                    case 3:
                        editorDataEntity.setPoster(matcher.group(1));
                        break;
                }
            }
        }
    }

    /*拆分文本数据*/
    private void splitTextData(String htmlStr) {
        String data = htmlStr;
        String attr;
        for (String aRegArray : regArray) {
            if (aRegArray.equals(EditorConstants.REGEX_ATTACHMENT))
                attr = EditorConstants.ATTR_HREF;
            else {
                attr = EditorConstants.ATTR_SRC;
            }
            data = RegexUtils.getFilterString(RegexUtils.getRegExpression(aRegArray, attr), data, EditorConstants.SPLITTAG);
        }
        addTextToEditList(data);
    }

    /*将文本数据装载到编辑器集合中*/
    private void addTextToEditList(String data) {
        String filterData = RegexUtils.getFilterString(EditorConstants.REGEX_HTML_ALL, data, "");
        String[] textData = filterData.split(EditorConstants.SPLITTAG);
        for (String aTextData : textData) {
            editShowList.add(new EditorDataEntity(RichTextEditor.TYPE_TEXT, aTextData));
        }
    }


    /**
     * 对多媒体资源位置进行排序
     */
    private void sortMediaPosition() {
        Collections.sort(editorDataList, new Comparator<EditorDataEntity>() {
            @Override
            public int compare(EditorDataEntity lhs, EditorDataEntity rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        });
        setMediaBitmap();
    }

    /*设置bitmap*/
    private void setMediaBitmap() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                try {
                    for (int i = 0; i < editorDataList.size(); i++) {
                        String url = editorDataList.get(i).getUrl();
                        if (!url.contains("http")) {
//                            url = Network.FILE_SERVER_COMMON_URL + url;
                        }
                        if (editorDataList.get(i).getType().equals(RichTextEditor.TYPE_IMAGE)) {
                            editorDataList.get(i).setBitmapResource(Glide.with(mContext).load(url).asBitmap().into(500, 500).get());
                        } else if (editorDataList.get(i).getType().equals(RichTextEditor.TYPE_VIDEO)) {
//                            editorDataList.get(i).setBitmapResource(Glide.with(mContext).load(Network.FILE_SERVER_COMMON_URL + editorDataList.get(i).getPoster()).asBitmap().into(500, 500).get());
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                //组合文本数据和多媒体数据
                for (int i = 0; i < editorDataList.size(); i++) {
                    if (editorDataList.get(i).getBitmapResource() == null) {
                        editorDataList.get(i).setBitmapResource(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.default_bg));
                    }
                    EditorDataEntity editData = new EditorDataEntity(editorDataList.get(i).getText(), editorDataList.get(i).getType(), editorDataList.get(i).getUrl(), editorDataList.get(i).getBitmapResource());
                    editData.setMediaPath(editData.getUrl());
                    int number = 2 * i + 1;
                    if (number > editShowList.size()) {
                        editShowList.add(editData);
                    } else {
                        editShowList.add(number, editData);
                    }
                }

                showData(isRichEditorStatus());
                if (editorBackground != 0) {
                    editor.setEditorBackground(editorBackground);
                }
            }
        }.execute();
    }

    /**
     * 添加图片到富文本剪辑器
     */
    public void insertBitmap(EditorDataEntity editorDataEntity) {
        editor.insertImage(editorDataEntity);
    }

    /**
     * 添加音频标签到富文本编辑器中
     */
    public void insertAudio(EditorDataEntity editorDataEntity) {
        editor.insertAudio(editorDataEntity);
    }

    /**
     * 获得多媒体个数
     *
     * @param type 视频或音频类型
     * @return 多媒体个个数
     */
    public int getMediaCount(String type) {
        int audioCount = 0;
        int videoCount = 0;
        List<EditorDataEntity> editorDataEntityList = editor.buildEditData();
        for (int i = 0; i < editorDataEntityList.size(); i++) {
            if (editorDataEntityList.get(i).getType().equals(RichTextEditor.TYPE_AUDIO)) {
                audioCount++;
                continue;
            }
            if (editorDataEntityList.get(i).getType().equals(RichTextEditor.TYPE_VIDEO)) {
                videoCount++;
            }
        }
        if (type.equals(RichTextEditor.TYPE_AUDIO)) {
            return audioCount;
        } else {
            return videoCount;
        }
    }

    public void setEditorHint(String hint) {
        editor.setEditHint(hint);
    }

    public void clearHint() {
        editor.clearHint();
    }

    public List<EditorDataEntity> getEditorDataList() {
        return editorDataList;
    }

    public List<EditorDataEntity> getEditShowList() {
        return editShowList;
    }

}
