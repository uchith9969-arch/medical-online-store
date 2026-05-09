package com.example.medical_online_store;

public class Review {
        private int id;
        private String name;
        private int rating;
        private String comment;

        // Default constructor for JSON mapping
        public Review() {}

        public Review(int id, String name, int rating, String comment) {
            this.id = id;
            this.name = name;
            this.rating = rating;
            this.comment = comment;
        }

        // Getters and Setters (Keep them as they are)
        public int getId() { return id; }
        public String getName() { return name; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }

        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRating(int rating) { this.rating = rating; }
        public void setComment(String comment) { this.comment = comment; }

        public String toFileString() {
            return id + "|" + name + "|" + rating + "|" + comment;
        }

        public static Review fromString(String line) {
            String[] p = line.split("\\|");
            return new Review(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3]);
        }
    }

