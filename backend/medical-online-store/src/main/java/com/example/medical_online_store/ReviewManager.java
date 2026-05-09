package com.example.medical_online_store;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
public class ReviewManager {
        private final String FILE_NAME = "reviews.txt";

        public List<Review> getAll() {
            List<Review> list = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = br.readLine()) != null) {
                    list.add(Review.fromString(line));
                }
            } catch (IOException e) { }
            return list;
        }

        public void saveAll(List<Review> list) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (Review r : list) {
                    bw.write(r.toFileString());
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void add(Review review) {
            List<Review> list = getAll();
            review.setId(list.size() + 1);
            list.add(review);
            saveAll(list);
        }

        public void delete(int id) {
            List<Review> list = getAll();
            list.removeIf(r -> r.getId() == id);
            saveAll(list);
        }

        public void update(int id, Review updatedReview) {
            List<Review> list = getAll();
            for (Review r : list) {
                if (r.getId() == id) {
                    r.setName(updatedReview.getName());
                    r.setRating(updatedReview.getRating());
                    r.setComment(updatedReview.getComment());
                }
            }
            saveAll(list);
        }
    }

