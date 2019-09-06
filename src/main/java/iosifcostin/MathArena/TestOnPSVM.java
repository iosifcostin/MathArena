package iosifcostin.MathArena;

import iosifcostin.MathArena.Service.UserService;
import iosifcostin.MathArena.model.User;
import org.modelmapper.internal.util.Lists;

import java.util.ArrayList;
import java.util.List;

public class TestOnPSVM {


    public static void main(String[] args) {

      String x = "https://matharena.s3.eu-central-1.amazonaws.com/";

      String m = x.replace("https://matharena.s3.eu-central-1.", "");

        System.out.println(m);
    }
}
