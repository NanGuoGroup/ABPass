package ABPassNew.Movies;

import java.util.ArrayList;

import ABPassNew.Verifier.Movie;

public class Movies {

	static private ArrayList<Movie> movies;

	private static void initMovies(String movieListJson){
		movies = Movie.getMovieListFromJson(movieListJson);
	    return;
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

