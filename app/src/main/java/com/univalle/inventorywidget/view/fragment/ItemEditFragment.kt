package com.univalle.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.univalle.inventorywidget.databinding.FragmentItemEditBinding

class ItemEditFragment : Fragment() {

    private var _binding: FragmentItemEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** -----------------------------------------
         *   CONFIGURACIÓN DE TOOLBAR (Criterio 1)
         * ----------------------------------------- */
        binding.toolbarEdit.title = "Editar producto"

        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        /** ---------------------------------------------------
         *  AQUÍ SE AGREGARÁ LO DEMÁS (LOS SIG. 13 CRITERIOS)
         * ---------------------------------------------------
         *
         *  Criterio 2 → ID solo lectura
         *  Criterio 3 → Cargar datos enviados desde el Home
         *  Criterio 4 → Actualizar en BD
         *  Criterio 5 → Habilitar botón Editar
         *  Criterio 6 → Validación campo nombre
         *  Criterio 7 → Validación precio
         *  Criterio 8 → Validación cantidad
         *  Criterio 9 → Evitar repetición de nombre
         *  Criterio 10 → Toast mensaje de actualización
         *  Criterio 11 → Confirmación diálogo
         *  Criterio 12 → Animación de carga
         *  Criterio 13 → Ocultar teclado
         *  Criterio 14 → Navegar al Home
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
