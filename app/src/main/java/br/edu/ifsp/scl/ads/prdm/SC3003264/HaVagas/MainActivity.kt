package br.edu.ifsp.scl.ads.prdm.SC3003264.HaVagas

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import br.edu.ifsp.scl.ads.prdm.SC3003264.HaVagas.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

public class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Campos principais
    private lateinit var editTextNome: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var checkBoxEmails: CheckBox
    private lateinit var radioGroupTelefoneTipo: RadioGroup
    private lateinit var editTextTelefone: TextInputEditText
    private lateinit var checkBoxAddCelular: CheckBox
    private lateinit var textInputCelular: TextInputLayout
    private lateinit var editTextCelular: TextInputEditText
    private lateinit var radioGroupSexo: RadioGroup
    private lateinit var editTextNascimento: TextInputEditText
    private lateinit var spinnerFormacao: Spinner
    private lateinit var layoutFormacao: LinearLayout
    private lateinit var editTextVagas: TextInputEditText
    private lateinit var buttonSalvar: Button
    private lateinit var buttonLimpar: Button

    // Campos dinâmicos
    private var editTextAnoFormatura: TextInputEditText? = null
    private var editTextAnoConclusao: TextInputEditText? = null
    private var editTextInstituicao: TextInputEditText? = null
    private var editTextTituloMonografia: TextInputEditText? = null
    private var editTextOrientador: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupSpinner()
        setupListeners()
    }

    private fun setupViews() {
        editTextNome = binding.editTextNome
        editTextEmail = binding.editTextEmail
        checkBoxEmails = binding.checkBoxEmails
        radioGroupTelefoneTipo = binding.radioGroupTelefoneTipo
        editTextTelefone = binding.editTextTelefone
        checkBoxAddCelular = binding.checkBoxAddCelular
        textInputCelular = binding.textInputCelular
        editTextCelular = binding.editTextCelular
        radioGroupSexo = binding.radioGroupSexo
        editTextNascimento = binding.editTextNascimento
        spinnerFormacao = binding.spinnerFormacao
        layoutFormacao = binding.layoutFormacao
        editTextVagas = binding.editTextVagas
        buttonSalvar = binding.buttonSalvar
        buttonLimpar = binding.buttonLimpar
    }

    private fun setupSpinner() {
        val formacoes = arrayOf(
            "Selecione sua formação",
            "Ensino Fundamental",
            "Ensino Médio",
            "Graduação",
            "Especialização",
            "Mestrado",
            "Doutorado"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formacoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormacao.adapter = adapter

        spinnerFormacao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updateCamposFormacao(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateCamposFormacao(formacaoIndex: Int) {
        layoutFormacao.removeAllViews()

        when (formacaoIndex) {
            1, 2 -> { // Fundamental e Médio
                addTextInput("Ano de formatura", "inputAnoFormatura").also { editTextAnoFormatura = it }
            }
            3, 4 -> { // Graduação e Especialização
                addTextInput("Ano de conclusão", "inputAnoConclusao").also { editTextAnoConclusao = it }
                addTextInput("Instituição", "inputInstituicao").also { editTextInstituicao = it }
            }
            5, 6 -> { // Mestrado e Doutorado
                addTextInput("Ano de conclusão", "inputAnoConclusao").also { editTextAnoConclusao = it }
                addTextInput("Instituição", "inputInstituicao").also { editTextInstituicao = it }
                addTextInput("Título da monografia", "inputTituloMonografia").also { editTextTituloMonografia = it }
                addTextInput("Orientador", "inputOrientador").also { editTextOrientador = it }
            }
            else -> {
                editTextAnoFormatura = null
                editTextAnoConclusao = null
                editTextInstituicao = null
                editTextTituloMonografia = null
                editTextOrientador = null
            }
        }
    }

    private fun addTextInput(hint: String, tag: String): TextInputEditText {
        val layout = TextInputLayout(this).apply {
            this.hint = hint
        }
        val editText = TextInputEditText(this).apply {
            inputType = if (hint.contains("Ano")) {
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            } else {
                InputType.TYPE_CLASS_TEXT
            }
            val tag = tag
        }
        layout.addView(editText)
        layoutFormacao.addView(layout)
        return editText
    }

    private fun setupListeners() {
        checkBoxAddCelular.setOnCheckedChangeListener { _, isChecked ->
            textInputCelular.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }

        buttonLimpar.setOnClickListener {
            limparFormulario()
        }

        buttonSalvar.setOnClickListener {
            if (validarCampos()) {
                mostrarResumo()
            }
        }
    }

    private fun validarCampos(): Boolean {
        val erros = mutableListOf<String>()

        if (editTextNome.text.isNullOrEmpty()) erros.add("Nome completo é obrigatório.")
        if (editTextEmail.text.isNullOrEmpty()) {
            erros.add("E-mail é obrigatório.")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text).matches()) {
            erros.add("E-mail inválido.")
        }
        if (editTextTelefone.text.isNullOrEmpty()) erros.add("Telefone é obrigatório.")
        if (checkBoxAddCelular.isChecked && editTextCelular.text.isNullOrEmpty()) {
            erros.add("Telefone celular é obrigatório se marcado.")
        }
        if (editTextNascimento.text.isNullOrEmpty()) erros.add("Data de nascimento é obrigatória.")
        if (spinnerFormacao.selectedItemPosition == 0) erros.add("Selecione uma formação.")

        when (spinnerFormacao.selectedItemPosition) {
            1, 2 -> if (editTextAnoFormatura?.text.isNullOrEmpty()) erros.add("Ano de formatura é obrigatório.")
            3, 4 -> {
                if (editTextAnoConclusao?.text.isNullOrEmpty()) erros.add("Ano de conclusão é obrigatório.")
                if (editTextInstituicao?.text.isNullOrEmpty()) erros.add("Instituição é obrigatória.")
            }
            5, 6 -> {
                if (editTextAnoConclusao?.text.isNullOrEmpty()) erros.add("Ano de conclusão é obrigatório.")
                if (editTextInstituicao?.text.isNullOrEmpty()) erros.add("Instituição é obrigatória.")
                if (editTextTituloMonografia?.text.isNullOrEmpty()) erros.add("Título da monografia é obrigatório.")
                if (editTextOrientador?.text.isNullOrEmpty()) erros.add("Orientador é obrigatório.")
            }
        }

        if (erros.isNotEmpty()) {
            Toast.makeText(this, erros.joinToString("\n"), Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun mostrarResumo() {
        val nome = editTextNome.text.toString()
        val email = editTextEmail.text.toString()
        val receberEmails = if (checkBoxEmails.isChecked) "Sim" else "Não"

        val tipoTelefone = when (radioGroupTelefoneTipo.checkedRadioButtonId) {
            R.id.radioResidencial -> "Residencial"
            R.id.radioComercial -> "Comercial"
            else -> "Não informado"
        }
        val telefone = editTextTelefone.text.toString()

        val celular = if (checkBoxAddCelular.isChecked) editTextCelular.text.toString() else "Não informado"

        val sexo = when (radioGroupSexo.checkedRadioButtonId) {
            R.id.radioMasculino -> "Masculino"
            R.id.radioFeminino -> "Feminino"
            R.id.radioOutro -> "Outro"
            else -> "Não informado"
        }

        val nascimento = editTextNascimento.text.toString()
        val formacao = spinnerFormacao.selectedItem.toString()
        val vagas = editTextVagas.text.toString()

        val detalhesFormacao = when (spinnerFormacao.selectedItemPosition) {
            1, 2 -> "Ano de formatura: ${editTextAnoFormatura?.text}"
            3, 4 -> "Ano de conclusão: ${editTextAnoConclusao?.text}, Instituição: ${editTextInstituicao?.text}"
            5, 6 -> "Ano: ${editTextAnoConclusao?.text}, Inst: ${editTextInstituicao?.text}, " +
                    "Título: ${editTextTituloMonografia?.text}, Orientador: ${editTextOrientador?.text}"
            else -> ""
        }

        val resumo = """
            |✅ **Cadastro Realizado com Sucesso!**
            |
            |👤 Nome: $nome
            |📧 E-mail: $email
            |📬 Receber e-mails: $receberEmails
            |📞 Telefone ($tipoTelefone): $telefone
            |📱 Celular: $celular
            |🚻 Sexo: $sexo
            |📅 Nascimento: $nascimento
            |🎓 Formação: $formacao
            |📝 Detalhes: $detalhesFormacao
            |💼 Vagas de interesse: $vagas
        """.trimMargin()

        AlertDialog.Builder(this)
            .setTitle("Resumo do Cadastro")
            .setMessage(resumo)
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun limparFormulario() {
        editTextNome.text?.clear()
        editTextEmail.text?.clear()
        checkBoxEmails.isChecked = false
        radioGroupTelefoneTipo.clearCheck()
        editTextTelefone.text?.clear()
        checkBoxAddCelular.isChecked = false
        textInputCelular.visibility = android.view.View.GONE
        radioGroupSexo.clearCheck()
        editTextNascimento.text?.clear()
        spinnerFormacao.setSelection(0)
        editTextVagas.text?.clear()
        layoutFormacao.removeAllViews()
        editTextAnoFormatura = null
        editTextAnoConclusao = null
        editTextInstituicao = null
        editTextTituloMonografia = null
        editTextOrientador = null
    }
}