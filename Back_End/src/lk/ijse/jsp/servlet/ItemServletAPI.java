package lk.ijse.jsp.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/pages/item")
public class ItemServletAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Content-Type", "application/json");

            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");
            String option = req.getParameter("option");

            switch (option){
                case "getAll":
                    resp.addHeader("Access-Control-Allow-Origin", "*");
                    PreparedStatement pstm = connection.prepareStatement("select * from Item");
                    ResultSet rst = pstm.executeQuery();

                    JsonArrayBuilder allItems = Json.createArrayBuilder();
                    while (rst.next()) {
                        String code = rst.getString(1);
                        String description = rst.getString(2);
                        String qty = rst.getString(3);
                        String unitPrice = rst.getString(4);

                        JsonObjectBuilder itemObject = Json.createObjectBuilder();
                        itemObject.add("code", code);
                        itemObject.add("description", description);
                        itemObject.add("qty", qty);
                        itemObject.add("unitPrice", unitPrice);
                        allItems.add(itemObject.build());
                    }
                    break;
                case "search":
                    String code1 = req.getParameter("code");
                    PreparedStatement pstm2 = connection.prepareStatement("select * from items where id=?");
                    pstm2.setObject(1, code1);
                    ResultSet rst2 = pstm2.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    JsonObjectBuilder objectBuilder1 = Json.createObjectBuilder();
                    if (rst2.next()) {
                        String ids = rst2.getString(1);
                        String description = rst2.getString(2);
                        String qty = rst2.getString(3);
                        String unitPrice = rst2.getString(4);

                        objectBuilder1.add("code", ids);
                        objectBuilder1.add("description", description);
                        objectBuilder1.add("unitPrice", unitPrice);
                        objectBuilder1.add("qty", qty);

                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(objectBuilder1.build());
                    break;
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String itemCode  = req.getParameter("itemCode");
        String itemName  = req.getParameter("itemName");
        String itemQty   = req.getParameter("itemQty");
        String itemPrice = req.getParameter("itemPrice");

        try {
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Content-Type", "application/json");
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm = connection.prepareStatement("insert into Item values(?,?,?,?)");
            pstm.setObject(1, itemCode );
            pstm.setObject(2, itemName );
            pstm.setObject(3, itemQty  );
            pstm.setObject(4, itemPrice);


            if (pstm.executeUpdate() > 0) {
                JsonObjectBuilder response = Json.createObjectBuilder();
                response.add("state", "Ok");
                response.add("message", "Successfully Added.!");
                response.add("data", "");
                resp.setStatus(200);
                resp.getWriter().print(response.build());
            }else{
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "fail");
                objectBuilder.add("message", "Item not added..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (ClassNotFoundException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        } catch (SQLException e) {
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        }
    }




    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        String itemCode  = jsonObject.getString("itemCode ");
        String itemName  = jsonObject.getString("itemName ");
        String itemQty   = jsonObject.getString("itemQty  ");
        String itemPrice = jsonObject.getString("itemPrice");

        try {
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Content-Type", "application/json");
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm3 = connection.prepareStatement("update Item set description=?,qtyOnHand=?,unitPrice=? where code=?");
            pstm3.setObject(3,itemCode );
            pstm3.setObject(1,itemName );
            pstm3.setObject(2,itemQty  );
            pstm3.setObject(4,itemPrice);
            if (pstm3.executeUpdate() > 0) {
                resp.getWriter().println("Item Updated..!");
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "success");
                objectBuilder.add("data", "");
                objectBuilder.add("message", "Item Deleted..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            }else{
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "fail");
                objectBuilder.add("message", "Item Deleted fail..!");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String itemCode = req.getParameter("itemCode");

        try {
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Content-Type", "application/json");
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "1234");

            PreparedStatement pstm2 = connection.prepareStatement("delete from Item where code=?");
            pstm2.setObject(1, itemCode);
            if (pstm2.executeUpdate() > 0) {
                resp.getWriter().println("Item Deleted..!");
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("status", "success");
                objectBuilder.add("message", "Item Deleted..!");
                objectBuilder.add("data", "");
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods", "PUT");
        resp.addHeader("Access-Control-Allow-Methods", "DELETE");
        resp.addHeader("Access-Control-Allow-Headers", "content-type");
    }



}
