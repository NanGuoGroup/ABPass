package com.hyyas.ouy.movies;

import java.util.ArrayList;

import ABPassNew.Verifier.Movie;

/**
 * Created by 41861 on 2017/6/28.
 */

public class Movies {

    static private ArrayList<Movie> movies;

    private static void initMovies(String movieListJson){
        movies = Movie.getMovieListFromJson(movieListJson);
        return;
//        for (int i = 0; i < 10; i++) {
//            Movie movie = new Movie();
//            for (int year = 1940; year < 2001; year++) {
//                String yearStr = Integer.toString(year);
//                movie.getPolicies().add(new Policy(AttributeEnum.BirthYeah, yearStr));
//            }
//            movie.getPolicies().add(new Policy(AttributeEnum.Identity, "DrivingLicense"));
//            movie.getPolicies().add(new Policy(AttributeEnum.Identity, "PassPort"));
//            movie.getPolicies().add(new Policy(AttributeEnum.Identity, "SocialSecurityNumber"));
//            movie.getPolicies().add(new Policy(AttributeEnum.MemberShip, "6_month"));
//            movie.getPolicies().add(new Policy(AttributeEnum.MemberShip, "1_year"));
//            movie.setName("movie" + Integer.toString(i));
//            movie.setSummary("hufhewghqulfgeiuoqgfiuewqiouehgfuiqgheolfgequgef");
////            movie.setImgUrl("p" + Integer.toString(i));
//            movie.setImgUrl("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1446249877,1706342175&fm=58&s=9FF569846A22BAD45CF8D9010300B0C9");
//            movie.setUrl("http://vfx.mtime.cn/Video/2017/06/14/mp4/170614015012938693.mp4");
//            movie.setWarning("满18周岁\r\n持有中国公民有效证件\r\n本网站3个月或1年会员\r\n是否确认观看.");
//            movies.add(movie);
//        }

    }

    public static ArrayList<Movie> getMovies(String movieListJson) {
        if (null == movies)
            initMovies(movieListJson);
        return movies;
    }

    public static Movie getMovieById(int id){
        return movies.get(id);
    }
}
