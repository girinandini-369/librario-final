package com.librario.dto;

public class AdminSummaryResponse {

    private long totalBooks;
    private long totalUsers;
    private long booksBorrowed;
    private long activeBorrowRecords;

    public AdminSummaryResponse(long totalBooks, long totalUsers, long booksBorrowed, long activeBorrowRecords) {
        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.booksBorrowed = booksBorrowed;
        this.activeBorrowRecords = activeBorrowRecords;
    }

    // Getters & setters
    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getBooksBorrowed() {
        return booksBorrowed;
    }

    public void setBooksBorrowed(long booksBorrowed) {
        this.booksBorrowed = booksBorrowed;
    }

    public long getActiveBorrowRecords() {
        return activeBorrowRecords;
    }

    public void setActiveBorrowRecords(long activeBorrowRecords) {
        this.activeBorrowRecords = activeBorrowRecords;
    }
}
