package adrian.belarte.listacomprabd.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import adrian.belarte.listacomprabd.R;
import adrian.belarte.listacomprabd.configuaraciones.Configuracion;
import adrian.belarte.listacomprabd.helpers.ProductHelper;
import adrian.belarte.listacomprabd.modelos.Producto;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductoVH> {
    private Context context;
    private List<Producto> objects;
    private int resource;

    private ProductHelper helper;
    private Dao<Producto,Integer> daoProductos;


    public ProductAdapter(Context context, List<Producto> objects, int resource) {
        this.context = context;
        this.objects = objects;
        this.resource = resource;
        helper = new ProductHelper(context, Configuracion.BD_NAME,null,Configuracion.BD_VERSION);
        if(helper != null){
            try {
                daoProductos = helper.getDaoProductos();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(context).inflate(resource,null);


        productView.setLayoutParams(
                new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        return new ProductoVH(productView);
    }


    @Override
    public void onBindViewHolder(@NonNull ProductoVH holder, int position) {
        Producto producto = objects.get(position);


        holder.lbNombre.setText(producto.getNombre());
        holder.lbTotal.setText(String.valueOf(producto.getTotal()));


        holder.imEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEditar(holder.getAdapterPosition()).show();
            }
        });


        holder.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarBorrar(holder.getAdapterPosition()).show();
            }
        });
    }
    //metodo para edirtar
    private AlertDialog confirmarEditar(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("MODIFICAR PRODUCTO");
        builder.setCancelable(false);


        View productView = LayoutInflater.from(context).inflate(R.layout.product_view_model, null);
        EditText txtNombre = productView.findViewById(R.id.txtNombreViewModel);
        EditText txtCantidad = productView.findViewById(R.id.txtcantidadViewModel);
        EditText txtPrecio = productView.findViewById(R.id.txtprecioViewModel);
        builder.setView(productView);


        Producto producto = objects.get(posicion);
        txtNombre.setText(producto.getNombre());
        txtCantidad.setText(String.valueOf(producto.getCantidad()));
        txtPrecio.setText(String.valueOf(producto.getPrecio()));


        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!txtNombre.getText().toString().isEmpty()&&
                        !txtCantidad.getText().toString().isEmpty() &&
                        !txtPrecio.getText().toString().isEmpty()){
                    producto.setNombre(txtNombre.getText().toString());
                    producto.setCantidad( Integer.parseInt(txtCantidad.getText().toString()));
                    producto.setPrecio(Float.parseFloat(txtPrecio.getText().toString()));
                    notifyItemChanged(posicion);

                    try {
                        daoProductos.update(producto);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return builder.create();
    }
    //metodo para borrar
    private AlertDialog confirmarBorrar(int posicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Estas seguro?");
        builder.setCancelable(false);


        builder.setNegativeButton("CANCELAR", null);
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //borrar de la BD
                try {
                    daoProductos.delete(objects);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                objects.remove(posicion);
                notifyItemRemoved(posicion);
            }
        });
        return builder.create();
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public class ProductoVH extends RecyclerView.ViewHolder{
        TextView lbNombre, lbTotal;
        ImageButton imEdit, imDelete;
        public ProductoVH(@NonNull View itemView) {
            super(itemView);
            lbNombre = itemView.findViewById(R.id.lbNombreProducViewHolder);
            lbTotal = itemView.findViewById(R.id.lbTotalProductViewHolder);


            imEdit = itemView.findViewById(R.id.imEditProductViewHolder);
            imDelete = itemView.findViewById(R.id.imDeleteProductViewHolder);
        }
    }
}

