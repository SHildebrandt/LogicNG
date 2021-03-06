///////////////////////////////////////////////////////////////////////////
//                   __                _      _   ________               //
//                  / /   ____  ____ _(_)____/ | / / ____/               //
//                 / /   / __ \/ __ `/ / ___/  |/ / / __                 //
//                / /___/ /_/ / /_/ / / /__/ /|  / /_/ /                 //
//               /_____/\____/\__, /_/\___/_/ |_/\____/                  //
//                           /____/                                      //
//                                                                       //
//               The Next Generation Logic Library                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////
//                                                                       //
//  Copyright 2015-2016 Christoph Zengler                                //
//                                                                       //
//  Licensed under the Apache License, Version 2.0 (the "License");      //
//  you may not use this file except in compliance with the License.     //
//  You may obtain a copy of the License at                              //
//                                                                       //
//  http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                       //
//  Unless required by applicable law or agreed to in writing, software  //
//  distributed under the License is distributed on an "AS IS" BASIS,    //
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      //
//  implied.  See the License for the specific language governing        //
//  permissions and limitations under the License.                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

package org.logicng.cardinalityconstraints;

import org.junit.Assert;
import org.junit.Test;
import org.logicng.collections.ImmutableFormulaList;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests for the at-most-one encoders.
 * @author Christoph Zengler
 * @version 1.0
 * @since 1.0
 */
public class CCAMOTest {

  private static final FormulaFactory f = new FormulaFactory();
  private static final CCAtMostOne pure = new CCAMOPure(f);
  private static final CCAtMostOne ladder = new CCAMOLadder(f);
  private static final CCAtMostOne product = new CCAMOProduct(f);


  @Test
  public void testCC0() {
    Assert.assertTrue(pure.build(new LinkedList<Variable>()).empty());
    Assert.assertTrue(ladder.build(new LinkedList<Variable>()).empty());
    Assert.assertTrue(product.build(new LinkedList<Variable>()).empty());
  }

  @Test
  public void testCC1() {
    final List<Variable> vars = new LinkedList<>(Collections.singletonList(f.variable("v0")));
    Assert.assertTrue(pure.build(vars).empty());
    Assert.assertTrue(ladder.build(vars).empty());
    Assert.assertTrue(product.build(vars).empty());
  }

  @Test
  public void testPure() {
    testCC(2, pure);
    testCC(10, pure);
    testCC(100, pure);
    testCC(250, pure);
    testCC(500, pure);
  }

  @Test
  public void testLadder() {
    testCC(2, ladder);
    testCC(10, ladder);
    testCC(100, ladder);
    testCC(250, ladder);
    testCC(500, ladder);
  }

  @Test
  public void testProduct() {
    testCC(2, product);
    testCC(10, product);
    testCC(100, product);
    testCC(250, product);
    testCC(500, product);
  }

  private void testCC(int numLits, final CCAtMostOne encoder) {
    final List<Variable> lits = new LinkedList<>();
    final Variable[] problemLits = new Variable[numLits];
    for (int i = 0; i < numLits; i++) {
      final Variable lit = f.variable("v" + i);
      lits.add(lit);
      problemLits[i] = lit;
    }
    final ImmutableFormulaList clauses = encoder.build(lits);
    final SATSolver solver = MiniSat.miniSat(f);
    solver.add(clauses);
    Assert.assertEquals(Tristate.TRUE, solver.sat());
    final List<Assignment> models = solver.enumerateAllModels(problemLits);
    Assert.assertEquals(numLits + 1, models.size());
    for (final Assignment model : models)
      Assert.assertTrue(model.positiveLiterals().size() <= 1);
  }
}
