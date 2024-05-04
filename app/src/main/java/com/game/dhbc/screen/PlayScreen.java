package com.game.dhbc.screen;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;


import com.game.dhbc.Database.Question;
import com.game.dhbc.Database.QuestionDatabase;
import com.game.dhbc.R;
import com.game.dhbc.adapter.InputKeyWordadapter;
import com.game.dhbc.adapter.keywordadapter;
import com.game.dhbc.databinding.PlayscreenBinding;

import com.game.dhbc.dialog.DialogCompleteFragment;
import com.game.dhbc.dialog.DialogSuggestFragment;
import com.game.dhbc.listener.keywordlistener;
import com.game.dhbc.utilities.Constants;
import com.game.dhbc.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayScreen extends AppCompatActivity implements DialogSuggestFragment.suggestListener, DialogCompleteFragment.okbtnlistener {
    PreferenceManager preferenceManager;
    PlayscreenBinding binding;
    int playMode = 1, heartone = 5, hearttwo = 5, diemSo2 = 0, ruby2 = 100, diemSo1 = 0, ruby1 = 100;
    int bellring = 0;
    CountDownTimer countDownTimer15 = new CountDownTimer(15000, 1000) { // 10 giây, mỗi giây đếm xuống 1
        public void onTick(long millisUntilFinished) {
            // Update UI với thời gian còn lại
            if (bellring == 1) {
                binding.belltwo.setVisibility(View.INVISIBLE);
            } else {
                binding.bellone.setVisibility(View.INVISIBLE);
            }
            binding.score.setText("" + millisUntilFinished / 1000);
            if(millisUntilFinished/1000==5)
            {
                binding.score.setTextColor(Color.parseColor("#FFFF0000"));
            }
        }

        public void onFinish() {
            // Khi đồng hồ đếm ngược kết thúc, cập nhật UI tương ứng
            binding.score.setTextColor(Color.parseColor("#FFFFFF"));

            if (bellring == 1) {
                binding.belltwo.setVisibility(View.VISIBLE);
            } else {
                binding.bellone.setVisibility(View.VISIBLE);
            }
            if (countinput < listkeyword.size()||countinput==listkeyword.size()) {
                if (bellring == 1) {
                    heartone -= 1;

                    binding.heartoneicon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shakeanimation));
                    binding.heart1.setText(String.valueOf(heartone));

                } else if (bellring == 2) {
                    hearttwo -= 1;
                    binding.heartoneicon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shakeanimation));

                    binding.heart2.setText(String.valueOf(hearttwo));

                }

            }

            bellring = 0;

        }
    };


    CountDownTimer countDownTimer90 = new CountDownTimer(90000, 1000) { // 10 giây, mỗi giây đếm xuống 1
        public void onTick(long millisUntilFinished) {
            // Update UI với thời gian còn lại
            binding.clock.setText("" + millisUntilFinished / 1000);
            if(millisUntilFinished/1000==10)
            {
                binding.score.setTextColor(Color.parseColor("#FFFF0000"));
            }
        }

        public void onFinish() {
            // Khi đồng hồ đếm ngược kết thúc, cập nhật UI tương ứng
            whoWin();
            binding.score.setTextColor(Color.parseColor("#FFFFFF"));
        }
    };

    ArrayList<Question> listquestions;//Chua danh sach cau hoi
    ArrayList<String> listinputkeyword;//Chua danh sach tu input
    ArrayList<String> listkeyword;//Danh sach tu khoa chinh xac
    InputKeyWordadapter rcvinputkeywordadapter;//Adapter cho recyclerview hien thi danh sach cac tu input
    keywordadapter rcvkeywordadapter;//Adapter cho recyclerview hien thi tu khoa da nhap
    private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//Chuoi alphabet
    int index = 0;//index la chi so cau hoi dang hien thi hien tai
    int ruby = 100;//ruby mac dinh la 100
    int countinput;//bien dem xem da nhap duoc bao nhieu tu
    int diemso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PlayscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getPlayMode();
        //ham khoi tao
        init();
        CreateImageandKeyword();
        header();

    }

    private void header() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.suggest.setOnClickListener(view -> {

            DialogSuggestFragment dialogSuggestFragment = new DialogSuggestFragment();
            Bundle bundle = new Bundle();
            String audiogoiy = listquestions.get(index).getAudiogoiy();
            bundle.putString("audiogoiy", audiogoiy);
            if (bellring == 0) {
                bundle.putInt("ruby", ruby);

            } else if (bellring == 1) {
                bundle.putInt("ruby", ruby1);
            } else if (bellring == 2) {
                bundle.putInt("ruby", ruby2);
            }
            dialogSuggestFragment.setArguments(bundle);
            if (playMode == 2) {
                if (bellring == 1 || bellring == 2) {
                    dialogSuggestFragment.show(getSupportFragmentManager(), null);

                } else {
                    MainActivityScreen.showToast("Vui lòng bấm chuông trước",getApplicationContext());                    }


            }
            if (playMode == 1) {
                dialogSuggestFragment.show(getSupportFragmentManager(), null);
            }


        });
    }

    private void CreateImageandKeyword() {
        binding.level.setText("LEVEL " + (index + 1) + "/" + (listquestions.size()));
        //đổ dữ liệu ảnh gif câu hỏi hiện tại ra Gifview
        String uri = listquestions.get(index).getImage();
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        binding.imagegame.setImageResource(imageResource);
        //Mặc định hiển thị ban đầu cho recyclerview các chữ cái ban đầu trống nên ta để các phần tử trong listkeyword là ""
        for (int i = 0; i < listquestions.get(index).getKeyword().length(); i++) {
            listkeyword.add("");
        }
        //Đếm số lượng chữ cái đã nhập. Mặc định countinput là độ dài của listkeyword.
        countinput = listkeyword.size();
        //Thiết lập bộ nhập câu hỏi, gồm 20 ô nhập câu hỏi
        for (int i = 0; i < listquestions.get(index).getKeyword().length(); i++) {
            //lấy ra các chữ cái bắt buộc phải có trong bộ nhập và thêm nó vào listinputkeyword
            listinputkeyword.add(listquestions.get(index).getKeyword().substring(i, i + 1));
        }
        //Bộ nhập gồm 20 chữ cái, sau khi thêm các chữ cái bắt buộc có, bộ nhập vẫn chưa đủ chữ cái tối thiểu
        //Những chữ cái còn lại sẽ chọn ngẫu nhiên từ chuỗi String alphabet, và thêm vào bộ nhập
        for (int i = 0; i < 20; i++) {
            if (listinputkeyword.size() < 14) {
                Random rnd = new Random();
                char randomChar = alphabet.charAt(rnd.nextInt(alphabet.length()));
                listinputkeyword.add(String.valueOf(randomChar));
            } else {
                break;
            }

        }
        //Sau khi bộ nhập đã đủ 20 phần tử, ta sẽ random vị trí các chữ cái trong bộ nhập
        Collections.shuffle(listinputkeyword);
        //Đổ dữ liệu bộ nhập ra màn hình
        //Khoi tao adapter
        rcvinputkeywordadapter = new InputKeyWordadapter(listinputkeyword, this, new keywordlistener() {
            @Override
            //phuong thuc xu li su kien khi click vao cac item cua recyclerview nhap keyword
            public void onClickkeywordlistener(int position) {
                //vong for duyet cac vi tri phan tu rong trong recyclerview hien thi dap an
                if (playMode == 1) {
                    onClickkeyword(position);
                }
                if (playMode == 2) {
                    if (bellring != 1 && bellring != 2) {
                       MainActivityScreen.showToast("Vui lòng bấm chuông trước",getApplicationContext());
                    } else {
                        onClickkeyword(position);
                    }
                }

            }
        });
        rcvkeywordadapter = new keywordadapter(listkeyword, this, new keywordlistener() {
            @Override
            public void onClickkeywordlistener(int position) {
                rcvkeywordadapter.animationcheck = false;
                if (listkeyword.get(position).equals("") == true) {
                } else {
                    listkeyword.set(position, "");

                    if (countinput < listkeyword.size()) {
                        countinput += 1;
                    }
                    rcvkeywordadapter.notifyDataSetChanged();
                }
            }
        });
        binding.rcvkeyword.setAdapter(rcvkeywordadapter);
        binding.rcvinputkeyword.setAdapter(rcvinputkeywordadapter);
        binding.rcvinputkeyword.setLayoutManager(new GridLayoutManager(getApplicationContext(), 7));
        binding.rcvkeyword.setLayoutManager(new GridLayoutManager(getApplicationContext(), 7));

        binding.rcvkeyword.setAdapter(rcvkeywordadapter);
        binding.rcvinputkeyword.setAdapter(rcvinputkeywordadapter);

        countDownTimer90.cancel();
        countDownTimer90.start();
        countDownTimer15.cancel();
        bellring = 0;
    }

    private void onClickkeyword(int position) {
        for (int i = 0; i < listkeyword.size(); i++) {
            //Neu trong listkeyword vẫn còn phần tử trống
            if (listkeyword.get(i).equals("") == true) {
                //Giảm 1 phần tử dếm countinput
                countinput = countinput - 1;
                //Set vị trí trống đó bằng giá trị của ô được click trong bộ nhập
                listkeyword.set(i, listinputkeyword.get(position));
                //Adapter lắng nghe thay đổi dữ liệu
                rcvkeywordadapter.notifyDataSetChanged();
                //Sau khi đổ dữ liệu xong, thoát khỏi vòng for
                break;
            }
        }
        //Kiểm tra xem người dùng đã nhập đến phần tử cuối cùng chưa
        if (countinput == 0) {
            //Nếu đã đến phần tử cuối cùng, kiểm tra xem đáp án đó có đúng không
            checkans();
        }
    }


    private void createlistQuestions() {
        //Lay toan bo cau hoi co trong database
        listquestions.addAll(QuestionDatabase.getInstance(getApplicationContext()).questionDao().getAllQuestion());
        //Kiem tra xem bo cau hoi da co trong database chưa, nếu listquestion.size()=0 tức là lần đầu chạy ứng dụng thì ta
        //Nếu chưa có câu hỏi nào trong database, Khởi tạo từng đối tượng câu hỏi. Và thêm nó vào database. Đồng thời thêm vào listquestions

        if (listquestions.size() == 0) {
            //Tao cau hoi baixich
            Question baixich = new Question();
            baixich.setImage("@drawable/baixich");
            baixich.setKeyword("BAIXICH");
            baixich.setAudiogoiy("baixich");
            baixich.setDapan("BÀI XÍCH");
            //Thêm câu hỏi vào database
            insertQuestionDatabase(baixich);
            //Thêm câu hỏi vào listquestion
            listquestions.add(baixich);
            Question baphai = new Question();
            baphai.setImage("@drawable/baphai");
            baphai.setKeyword("BAPHAI");
            baphai.setAudiogoiy("baphai");
            baphai.setDapan("BA PHẢI");
            insertQuestionDatabase(baphai);
            listquestions.add(baphai);
            Question chanhcom = new Question();
            chanhcom.setImage("@drawable/chanhcom");
            chanhcom.setKeyword("CHANHCOM");
            chanhcom.setAudiogoiy("chanhcom");
            chanhcom.setDapan("CHANH CỐM");
            insertQuestionDatabase(chanhcom);
            listquestions.add(chanhcom);
        }
        //Random listquestion, vì mỗi lần chơi,thứ tự câu hỏi được hiển thị sẽ khác nhau. Nên cần random
        Collections.shuffle(listquestions);
    }


    private void init() {
        getData();
        listquestions = new ArrayList<>();
        listkeyword = new ArrayList<>();
        listinputkeyword = new ArrayList<>();
        createlistQuestions();


    }

    private void    getData() {
        preferenceManager = new PreferenceManager(this);
        //lay du lieu ruby da luu trong may
        ruby = preferenceManager.getruby(Constants.KEY_RUBY);
        if(playMode==1){
            if(ruby==0)
            {
                ruby=25;
                preferenceManager.putInt(Constants.KEY_RUBY,ruby);
            }
            binding.ruby.setText(String.valueOf(ruby));

        }
        else if(playMode==2) {
            binding.ruby.setText(String.valueOf(ruby1));

        }

        binding.score.setText(String.valueOf(diemso));
    }

    ;

    //Hàm kiểm tra đáp án
    private void checkans() {
        //Nếu người dùng, đang sử dụng gợi ý. Dừng gợi ý lại để kiểm tra đáp án
        if (MainActivityScreen.mediaPlayer != null) {
            if (MainActivityScreen.mediaPlayer.isPlaying()) {
                MainActivityScreen.mediaPlayer.stop();
                MainActivityScreen.mediaPlayer.release();
                MainActivityScreen.mediaPlayer = null;
            }
        }
        //Stringbuilder ghép các chữ cái trong listkeyword thành 1 đáp án hoàn thiện
        StringBuilder sb = new StringBuilder();
        // Creating a string using append() method
        for (int i = 0; i < listkeyword.size(); i++) {
            sb.append(listkeyword.get(i));
        }
        //Sau khi được đáp án hoàn thiện, so sánh với đáp án của câu hỏi
        //Nếu đó là câu trả lời đúng
        if (sb.toString().equals(listquestions.get(index).getKeyword()) == true) {
            // tăng điểm số và ruby
            if (bellring == 1) {
                diemSo1 += 5;
                ruby1 += 25;
            } else if (bellring == 2) {
                diemSo2 += 5;
                ruby2 += 25;

            } else {
                diemso += 5;
                ruby += 25;
            }
            //Hiển thị dialog chúc mừng chiến thắng
            DialogCompleteFragment dialogCompleteFragment = new DialogCompleteFragment();
            //Chuyển dữ liệu điểm số ruby level đáp án vào Fragment Dialog
            Bundle b = new Bundle();
            b.putInt("diemso", diemso);
            b.putInt("level", index);
            b.putInt("ruby", ruby);
            b.putString("dapan", listquestions.get(index).getDapan());
            dialogCompleteFragment.setArguments(b);
            dialogCompleteFragment.show(getSupportFragmentManager(), null);
            if (bellring == 0) {
                //Sau khi được tăng điểm do trả lời đúng, kiểm tra xem điểm số đó đã phá vỡ kỷ lục chưa
                //Nếu lớn hơn kỷ lục cũ, thêm vào cơ sở dữ liệu
                if (diemso > preferenceManager.getkyluc(Constants.KEY_KYLUC)) {
                    preferenceManager.putInt(Constants.KEY_KYLUC, diemso);
                }
                preferenceManager.putInt(Constants.KEY_RUBY, ruby);
                //Đổ dữ liệu ra Dialog
                phanThuong(bellring, String.valueOf(ruby), String.valueOf(diemso));
            } else if (bellring == 1) {
                phanThuong(bellring, String.valueOf(ruby1), String.valueOf(diemSo1));
            } else {
                phanThuong(bellring, String.valueOf(ruby2), String.valueOf(diemSo2));
            }

            bellring = 0;

        } else {
            //Nếu người dùng trả lời sai, chạy animationcheck(), thông báo nhập sai
            rcvkeywordadapter.animationcheck = true;
            rcvkeywordadapter.notifyDataSetChanged();
            if (bellring == 1 || bellring == 2) {
                countDownTimer15.cancel();
                binding.score.setText(String.valueOf(0));
            }

            if (bellring == 1) {
                heartone -= 1;
                binding.heart1.setText(String.valueOf(heartone));
                binding.belltwo.setVisibility(View.VISIBLE);
                if (heartone == 0) {
                    whoWin();
                }
            } else if (bellring == 2) {
                hearttwo -= 1;
                binding.heart2.setText(String.valueOf(hearttwo));
                binding.bellone.setVisibility(View.VISIBLE);

                if (hearttwo == 0) {
                    whoWin();
                }

            }
            bellring = 0;


        }
    }

    private void whoWin() {
        Intent i = new Intent(getApplicationContext(), WingameScreen.class);
        if (bellring != 0) {
            if (diemSo1 > diemSo2) {
                i.putExtra("whowin", "PLAYER1!!!");
            } else if (diemSo1 < diemSo2) {
                i.putExtra("whowin", "PLAYER2!!!");
            } else {
                i.putExtra("whowin", "Hòa!!!");

            }
        } else {

            i.putExtra("whowin", "LEVEL: "+(index+1)+"/"+listquestions.size()+"!!!");
        }
        startActivity(i);
        finish();
    }

    //Hàm nextquestion, chuyển câu hỏi khác
    private void nextquestion() {
        if(playMode==2) {
            binding.score.setText(String.valueOf(0));
        }

        //Kiểm tra xem câu hỏi hiện tại đã là câu hỏi cuối cùng chưa, nếu chưa, tăng index lên 1, ngược lại chuyển sang màn hình
        //thắng game
        if (index < listquestions.size() - 1) {
            index += 1;
            //Khởi tạo lại bộ câu hỏi mới
            listinputkeyword.clear();
            listkeyword.clear();
            CreateImageandKeyword();
        } else {
            if(playMode==2)
            {
            whoWin();}
            else {
                startActivity(new Intent(getApplicationContext(),WingameScreen.class));
            }
            finish();
        }
    }


    //Phương thức xác nhận gợi ý
    @Override
    public void suggest(boolean yes, String audiogoiy) {
        if (yes == true) {
            if (playMode == 1) {

                if (suggestRuby(ruby, audiogoiy) == true) {
                    ruby-=25;
                    preferenceManager.putInt(Constants.KEY_RUBY, ruby);
                    binding.ruby.setText(String.valueOf(ruby));

                }
            }
            else {
                if(bellring==1)
                {
                   if( suggestRuby(ruby1,audiogoiy)==true)
                   {
                       ruby1 -=25;
                       binding.ruby.setText(String.valueOf(ruby1));
                   }
                }
                else if(bellring==2)
                {
                    if( suggestRuby(ruby2,audiogoiy)==true)
                    {
                        ruby2 -=25;
                        binding.tworuby.setText(String.valueOf(ruby2));

                    };
                }
            }
        }
    }

    private boolean suggestRuby(int checkRuby, String audiogoiy) {
        //Neu ruby>0
        if (checkRuby > 0) {
            int resID = getResources().getIdentifier(audiogoiy, "raw", getPackageName());
            //Kiem tra xem co audio goi y nao dang chay khong, neu co thi dung no lai
            if (MainActivityScreen.mediaPlayer != null) {
                if (MainActivityScreen.mediaPlayer.isPlaying()) {
                    MainActivityScreen.mediaPlayer.stop();
                    MainActivityScreen.mediaPlayer.release();
                    MainActivityScreen.mediaPlayer = null;
                }
            }
            //Chay goi y moi
            MainActivityScreen.mediaPlayer = MediaPlayer.create(getApplicationContext(), resID);
            MainActivityScreen.backgroundSound.pause();
            MainActivityScreen.mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                MainActivityScreen.backgroundSound.start();
            });
            MainActivityScreen.mediaPlayer.start();
            return true;

        }
        //Neu ruby =0;
        else {
            MainActivityScreen.showToast("Hết ruby rồi",getApplicationContext());
            return false;
        }
    }

    public void getPlayMode() {
        Log.d("kiop", String.valueOf(getIntent().getIntExtra("playmode", 3)));
        playMode = getIntent().getIntExtra("playmode", 1);
        if (playMode == 1) {
            oneMode();
        }
        if (playMode == 2) {
            twoMode();
        }

    }

    private void twoMode() {
        binding.twoPlayer.setVisibility(View.VISIBLE);
        binding.twoMode.setVisibility(View.VISIBLE);
        binding.heart1.setText(String.valueOf(heartone));
        binding.heart2.setText(String.valueOf(hearttwo));
        binding.tworuby.setText(String.valueOf(ruby2));
        binding.ruby.setText(String.valueOf(ruby1));
        binding.bellone.setOnClickListener(view -> {

            for (int i = 0; i < listquestions.get(index).getKeyword().length(); i++) {

                listkeyword.set(i, "");

            }
            countinput = listkeyword.size();
            rcvkeywordadapter.notifyDataSetChanged();
            rcvkeywordadapter.animationcheck = false;

            bellring = 1;

            countDownTimer15.start();

        });
        binding.belltwo.setOnClickListener(view -> {
            for (int i = 0; i < listkeyword.size(); i++) {
                listkeyword.set(i, "");

            }
            countinput = listkeyword.size();
            rcvkeywordadapter.notifyDataSetChanged();
            rcvkeywordadapter.animationcheck = false;

            bellring = 2;
            countDownTimer15.start();
        });
    }

    private void oneMode() {
        binding.twoPlayer.setVisibility(View.GONE);
        binding.twoMode.setVisibility(View.INVISIBLE);

    }

    private void phanThuong(int bellring, String a, String b) {
        if (bellring == 0) {
            binding.ruby.setText(a);
            binding.score.setText(b);
        } else if (bellring == 1) {
            binding.ruby.setText(a);
            binding.scoreonepl.setText(b);
        } else {
            binding.tworuby.setText(a);
            binding.scoretwopl.setText(b);
        }
    }


    @Override
    public void onClicknextquestion() {

        binding.belltwo.setVisibility(View.VISIBLE);

        binding.bellone.setVisibility(View.VISIBLE);
        nextquestion();
    }

    //ham them cau hoi vao database
    public void insertQuestionDatabase(Question question) {
        QuestionDatabase.getInstance(getApplicationContext()).questionDao().insert(question);
    }


    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer90.cancel();
        countDownTimer15.cancel();
    }
}
