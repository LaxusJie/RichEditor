package com.tb.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmojiUtil {
    private static ArrayList<Emoji> emojiList;

    public static ArrayList<Emoji> getEmojiList() {
        if (emojiList == null) {
            emojiList = generateEmojis();
        }
        return emojiList;
    }

    private static ArrayList<Emoji> generateEmojis() {
        ArrayList<Emoji> list = new ArrayList<>();
        for (int i = 0; i < EmojiResArray.length; i++) {
            Emoji emoji = new Emoji();
            emoji.setImageUri(EmojiResArray[i]);
            emoji.setContent(EmojiTextArray[i]);
            list.add(emoji);
        }
        return list;
    }


    public static final int[] EmojiResArray = {
            R.drawable.emoji1,
            R.drawable.emoji2,
            R.drawable.emoji3,
            R.drawable.emoji4,
            R.drawable.emoji5,
            R.drawable.emoji6,
            R.drawable.emoji7,
            R.drawable.emoji8,
            R.drawable.emoji9,
            R.drawable.emoji10,
            R.drawable.emoji11,
            R.drawable.emoji12,
            R.drawable.emoji13,
            R.drawable.emoji14,
            R.drawable.emoji15,
            R.drawable.emoji16,
            R.drawable.emoji17,
            R.drawable.emoji18,
            R.drawable.emoji19,
            R.drawable.emoji20,
            R.drawable.emoji21,
            R.drawable.emoji22,
            R.drawable.emoji23,
            R.drawable.emoji24,
            R.drawable.emoji25,
            R.drawable.emoji26,
            R.drawable.emoji27,
            R.drawable.emoji28,
            R.drawable.emoji29,
            R.drawable.emoji30,
            R.drawable.emoji31,
            R.drawable.emoji32,
            R.drawable.emoji33,
            R.drawable.emoji34,
            R.drawable.emoji35,
            R.drawable.emoji36,
            R.drawable.emoji37,
            R.drawable.emoji38,
            R.drawable.emoji39,
            R.drawable.emoji40,
            R.drawable.emoji41,
            R.drawable.emoji42,
            R.drawable.emoji43,
            R.drawable.emoji44,
            R.drawable.emoji45,
            R.drawable.emoji46,
            R.drawable.emoji47,
            R.drawable.emoji48,
            R.drawable.emoji49,
            R.drawable.emoji50,
            R.drawable.emoji51,
            R.drawable.emoji52,
            R.drawable.emoji53,
            R.drawable.emoji54,
            R.drawable.emoji55,
            R.drawable.emoji56,
            R.drawable.emoji57,
            R.drawable.emoji58,
            R.drawable.emoji59,
            R.drawable.emoji60,
            R.drawable.emoji61,
            R.drawable.emoji62,
            R.drawable.emoji63,
            R.drawable.emoji64,
            R.drawable.emoji65,
            R.drawable.emoji66,
            R.drawable.emoji67,
            R.drawable.emoji68,
            R.drawable.emoji69,
            R.drawable.emoji70,
            R.drawable.emoji71,
            R.drawable.emoji72,
            R.drawable.emoji73,
            R.drawable.emoji74,
            R.drawable.emoji75,
            R.drawable.emoji76,
            R.drawable.emoji77,
            R.drawable.emoji78,
            R.drawable.emoji79,
            R.drawable.emoji80,
            R.drawable.emoji81,
            R.drawable.emoji82,
            R.drawable.emoji83,
            R.drawable.emoji84,
            R.drawable.emoji85,
            R.drawable.emoji86,
            R.drawable.emoji87,
            R.drawable.emoji88,
            R.drawable.emoji89,
            R.drawable.emoji90,
            R.drawable.emoji91,
            R.drawable.emoji92,
            R.drawable.emoji93,
            R.drawable.emoji94,
            R.drawable.emoji95,
            R.drawable.emoji96,
            R.drawable.emoji97,
            R.drawable.emoji98,
            R.drawable.emoji99
    };

    public static final String[] EmojiTextArray = {
            "[/emoji1]",
            "[/emoji2]",
            "[/emoji3]",
            "[/emoji4]",
            "[/emoji5]",
            "[/emoji6]",
            "[/emoji7]",
            "[/emoji8]",
            "[/emoji9]",
            "[/emoji10]",
            "[/emoji11]",
            "[/emoji12]",
            "[/emoji13]",
            "[/emoji14]",
            "[/emoji15]",
            "[/emoji16]",
            "[/emoji17]",
            "[/emoji18]",
            "[/emoji19]",
            "[/emoji20]",
            "[/emoji21]",
            "[/emoji22]",
            "[/emoji23]",
            "[/emoji24]",
            "[/emoji25]",
            "[/emoji26]",
            "[/emoji27]",
            "[/emoji28]",
            "[/emoji29]",
            "[/emoji30]",
            "[/emoji31]",
            "[/emoji32]",
            "[/emoji33]",
            "[/emoji34]",
            "[/emoji35]",
            "[/emoji36]",
            "[/emoji37]",
            "[/emoji38]",
            "[/emoji39]",
            "[/emoji40]",
            "[/emoji41]",
            "[/emoji42]",
            "[/emoji43]",
            "[/emoji44]",
            "[/emoji45]",
            "[/emoji46]",
            "[/emoji47]",
            "[/emoji48]",
            "[/emoji49]",
            "[/emoji50]",
            "[/emoji51]",
            "[/emoji52]",
            "[/emoji53]",
            "[/emoji54]",
            "[/emoji55]",
            "[/emoji56]",
            "[/emoji57]",
            "[/emoji58]",
            "[/emoji59]",
            "[/emoji60]",
            "[/emoji61]",
            "[/emoji62]",
            "[/emoji63]",
            "[/emoji64]",
            "[/emoji65]",
            "[/emoji66]",
            "[/emoji67]",
            "[/emoji68]",
            "[/emoji69]",
            "[/emoji70]",
            "[/emoji71]",
            "[/emoji72]",
            "[/emoji73]",
            "[/emoji74]",
            "[/emoji75]",
            "[/emoji76]",
            "[/emoji77]",
            "[/emoji78]",
            "[/emoji79]",
            "[/emoji80]",
            "[/emoji81]",
            "[/emoji82]",
            "[/emoji83]",
            "[/emoji84]",
            "[/emoji85]",
            "[/emoji86]",
            "[/emoji87]",
            "[/emoji88]",
            "[/emoji89]",
            "[/emoji90]",
            "[/emoji91]",
            "[/emoji92]",
            "[/emoji93]",
            "[/emoji94]",
            "[/emoji95]",
            "[/emoji96]",
            "[/emoji97]",
            "[/emoji98]",
            "[/emoji99]"
    };

    static {
        emojiList = generateEmojis();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static void fitEmojiText(TextView comment, CharSequence content, Context context) {
        try {
            EmojiUtil.handlerEmojiText(comment, content, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handlerEmojiText(TextView comment, CharSequence content, Context context) throws IOException {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emoji = null;
        while (m.find()) {
            iterator = emojiList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoji = iterator.next();
                if (tempText.equals(emoji.getContent())) {
                    //转换为Span并设置Span的大小
                    sb.setSpan(new ImageSpan(context, decodeSampledBitmapFromResource(context.getResources(), emoji.getImageUri()
                                    , dip2px(context, 28), dip2px(context, 28))),
                            m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        comment.setText(sb);
    }

    /*删除表情的动作*/
    public static void deleteEmojiAction(EditText etInput,Context context){
        String text = etInput.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
            int index = text.lastIndexOf("[");
            if (index == -1) {
                int action = KeyEvent.ACTION_DOWN;
                int code = KeyEvent.KEYCODE_DEL;
                KeyEvent event = new KeyEvent(action, code);
                etInput.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                displayTextAction(etInput,context);
                return;
            }
            etInput.getText().delete(index, text.length());
            displayTextAction(etInput,context);
            return;
        }
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        etInput.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    /*显示表情动作*/
    public static void displayTextAction(EditText etInput,Context context){
        try {
            EmojiUtil.handlerEmojiText(etInput, etInput.getText().toString(), context);
            etInput.setSelection(etInput.getText().toString().length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*表情的点击动作*/
    public static void clickEmojiAction(Emoji emoji,EditText etInput,Context context){
        try{
            if (emoji != null) {
                int index = etInput.getSelectionStart();
                Editable editable = etInput.getEditableText();
                if (index < 0) {
                    editable.append(emoji.getContent());
                    EmojiUtil.handlerEmojiText(etInput, etInput.getText().toString(), context);
                    etInput.setSelection(etInput.getText().toString().length());
                } else {
                    editable.insert(index, emoji.getContent());
                    EmojiUtil.handlerEmojiText(etInput, etInput.getText().toString(), context);
                    etInput.setSelection(index+emoji.getContent().toString().length());
                }
                Log.e("文本框数据为:",etInput.getText().toString());

            }
//            displayTextAction(etInput,context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
